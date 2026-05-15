# 1. Alapbeállítások (Provider)
provider "google" {
  project = "testterraform-485917"
  region  = "us-east1"
  zone    = "us-east1-b"
}

# --- API-k automatikus engedélyezése ---

# Compute Engine API (VM-ekhez és hálózathoz)
resource "google_project_service" "compute_api" {
  service            = "compute.googleapis.com"
  disable_on_destroy = false # Ha törlöd a projektet terraformmal, ne kapcsolja ki az API-t
}

# Cloud Run API (a konténerek futtatásához)
resource "google_project_service" "run_api" {
  service            = "run.googleapis.com"
  disable_on_destroy = false
}

# IAM API (a jogosultságok, pl. allUsers/noauth beállításához)
resource "google_project_service" "iam_api" {
  service            = "iam.googleapis.com"
  disable_on_destroy = false
}

# --- Fontos módosítás a hálózatnál ---
# Meg kell várnunk, amíg a Compute API aktív lesz, mielőtt olvasni próbálnánk a hálózatot
data "google_compute_network" "default" {
  name       = "default"
  depends_on = [google_project_service.compute_api]
}

# 3. TŰZFAL: SSH elzárása (IAP engedélyezése)
# Ez csak a Google IAP tartományát engedi be a 22-es porton
resource "google_compute_firewall" "allow_ssh_iap" {
  name    = "allow-ssh-iap"
  network = data.google_compute_network.default.name

  allow {
    protocol = "tcp"
    ports    = ["22"]
  }

  source_ranges = ["35.235.240.0/20"] # Google IAP tartomány
  target_tags   = ["mysql-server"]
}

# 4. TŰZFAL: Belső MySQL elérés
# Ez engedi, hogy a Cloud Run (és a belső háló) elérje a 3306-ot
resource "google_compute_firewall" "allow_internal_mysql" {
  name    = "allow-internal-mysql"
  network = data.google_compute_network.default.name

  allow {
    protocol = "tcp"
    ports    = ["3306"]
  }

  source_ranges = ["10.0.0.0/8"] # Belső hálózati tartomány
  target_tags   = ["mysql-server"]
}

# 5. A MySQL Szerver (VM)
resource "google_compute_instance" "mysql_server" {
  name         = "mysql-server"
  machine_type = "e2-micro"
  tags         = ["mysql-server"]

  boot_disk {
    initialize_params {
      image = "debian-cloud/debian-11"
      size  = 30  # Itt állítjuk be a 30GB-ot az ingyenes kerethez
      type  = "pd-standard" # HDD (Ingyenes típus)
    }
  }

  network_interface {
    network = data.google_compute_network.default.name

    # Ezzel kap külső IP-t (ami kell a frissítésekhez), de a tűzfal védi
    access_config {}
  }

  # AUTOMATIKUS SWAP BEÁLLÍTÁS
  # Ez a script lefut minden indításkor, és megcsinálja a swap fájlt, ha nincs.
  metadata_startup_script = <<-EOT
    #!/bin/bash
    if [ ! -f /swapfile ]; then
      fallocate -l 2G /swapfile
      chmod 600 /swapfile
      mkswap /swapfile
      swapon /swapfile
      echo '/swapfile none swap sw 0 0' >> /etc/fstab
    fi
  EOT
}

# --- 6. BACKEND SERVICE (Cloud Run) ---
resource "google_cloud_run_service" "backend" {
  name     = "backend-service"
  depends_on = [google_project_service.run_api, google_project_service.compute_api]
  location = "us-east1" # Fontos: Ugyanaz a régió, mint a VM!

  template {
    metadata {
      annotations = {
        # EZ A KULCS: Direct VPC Egress beállítása (amit kézzel kerestünk)
        # Ez köti össze a backendet a belső hálózattal, hogy lássa a 10.142.0.3-at
        "run.googleapis.com/network-interfaces" = jsonencode([
          {
            network    = "default"
            subnetwork = "default"
            tags       = ["backend-service"]
          }
        ])
        # Csak a privát IP-kre (10.x.x.x) irányítsuk a VPC-be a forgalmat
        "run.googleapis.com/vpc-access-egress" = "private-ranges-only"
      }
    }

    spec {
      containers {
        # Ide írd a Docker képed jelenlegi címét!
        # Pl: us-east1-docker.pkg.dev/PROJECT_ID/repo/backend:latest
        image = "gcr.io/google-samples/hello-app:1.0"

        # Környezeti változók (hogy ne a kódba égesd az IP-t)
        env {
          name  = "SPRING_DATASOURCE_URL"
          value = "jdbc:mysql://10.142.0.3:3306/adatbazis_neve" # BELSŐ IP!
        }
        env {
          name  = "SPRING_DATASOURCE_USERNAME"
          value = "root"
        }
        env {
          name  = "SPRING_DATASOURCE_PASSWORD"
          value = "A_TITKOS_JELSZAVAD"
        }

        resources {
           limits = {
             memory = "512Mi" # Cloud Run memória limit
             cpu    = "1000m"
           }
        }
      }
    }
  }

  traffic {
    percent         = 100
    latest_revision = true
  }
}

# Backend publikussá tétele (hogy a Frontend elérje)
# Ha nem akarod publikusra, ezt a részt töröld!
data "google_iam_policy" "noauth" {
  binding {
    role = "roles/run.invoker"
    members = [
      "allUsers",
    ]
  }
}

resource "google_cloud_run_service_iam_policy" "noauth_backend" {
  location    = google_cloud_run_service.backend.location
  project     = google_cloud_run_service.backend.project
  service     = google_cloud_run_service.backend.name
  policy_data = data.google_iam_policy.noauth.policy_data
}

# --- 7. FRONTEND SERVICE (Cloud Run) ---
resource "google_cloud_run_service" "frontend" {
  name     = "frontend-service"
  depends_on = [google_project_service.run_api]
  location = "us-east1"

  template {
    spec {
      containers {
        image = "gcr.io/google-samples/hello-app:1.0" # Ide a frontend képed címe kell

        # Ha a React build időben kapja a változókat, ez itt nem biztos, hogy kell,
        # de ha Node.js szerverrel futtatod, akkor itt adhatod át a Backend URL-t.
        env {
          name  = "REACT_APP_API_URL"
          value = google_cloud_run_service.backend.status[0].url
        }
      }
    }
  }
}

# Frontend publikussá tétele (hogy a böngészőből elérd)
resource "google_cloud_run_service_iam_policy" "noauth_frontend" {
  location    = google_cloud_run_service.frontend.location
  project     = google_cloud_run_service.frontend.project
  service     = google_cloud_run_service.frontend.name
  policy_data = data.google_iam_policy.noauth.policy_data
}