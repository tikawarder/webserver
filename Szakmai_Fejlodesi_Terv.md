# Szakmai Fejlődési Terv: Medior Java Fejlesztővé Válás

A terv célja, hogy a jelenlegi szintről a piaci elvárásoknak megfelelő, stabil Medior Java Fejlesztő szintre lépjünk, figyelembe véve a magyar és a nemzetközi (kifejezetten a svájci) piaci elvárásokat.

## 1. Jelenlegi Szint (CV és LinkedIn alapján)
*   **Tapasztalat:** ~5 év Java fejlesztői tapasztalat (Liferay, EPAM, Nokia) és több mint 3 év Quality Engineer múlt.
*   **Tech Stack:** Java, Spring Boot, CI/CD (Jenkins), Docker, Kubernetes, MySQL.
*   **Módszertan:** Scrum, Agilis szoftverfejlesztés, minőségfókusz (Quality First).
*   **Soft Skillek:** Proaktív problémamegoldás, magabiztos angol kommunikáció, csapatmunka.

## 2. Piaci Elvárások (Magyarország vs. Svájc)

A Profession.hu és a LinkedIn hirdetései alapján a Medior szintű elvárások a következők:

### Magyarország (Általános Medior Java elvárások)
*   **Backend & Keretrendszer:** Java 11/17+, Spring Boot, Hibernate/JPA.
*   **Architektúra:** Microservices szemlélet, RESTful API tervezés, elosztott rendszerek ismerete.
*   **Adatbázisok:** Relációs adatbázisok mélyebb ismerete (pl. PostgreSQL, Oracle).
*   **Eszközök:** Git, Maven/Gradle, CI/CD (Jenkins, GitLab CI), Docker.
*   **Minőségbiztosítás:** Unit és Integrációs tesztelés (JUnit, Mockito), tiszta kód (Clean Code).
*   **Egyéb:** Messaging rendszerek (Kafka, RabbitMQ) ismerete. **Frontend (JavaScript/TypeScript + React)** alapismeretek egyre gyakrabban elvártak (erős "Full-Stack" eltolódás a piacon).

### Svájc (Különbségek és Kiemelt Elvárások)
*   **Nagyfokú önállóság:** Medior szinten is elvárás a teljes modulok felelősségteljes, önálló lefejlesztése és menedzselése.
*   **Kódminőség és Security:** Szigorú compliance és biztonsági követelmények, különösen banki/pénzügyi (FinTech) és egészségügyi szektorban. TDD (Test-Driven Development) és Clean Code szinte kötelező alapelvárás.
*   **Cloud technológiák:** A konténerizáció (Docker/K8s) mellett az AWS, Azure vagy Google Cloud platformok ismerete szinte mindenhol alap.
*   **Nyelv:** Bár az angol az IT nyelve, a német vagy francia nyelvtudás komoly versenyelőnyt jelent (vagy akár feltétel is lehet).
*   **Vízum/Engedélyek:** Az EU-n kívüli jelentkezőknek szigorúbb az elbírálás, ami növeli a szakmai kiválóság fontosságát.

## 3. GAP Analízis (Miben kell fejlődni?)
Az önbevallás/LinkedIn és a piaci elvárások összevetése alapján a következő területekre kell fókuszálni a tanulás során:
1.  **Tesztelés és TDD:** A QA múlt hatalmas előny, de kód szinten (JUnit 5, Mockito, TestContainers) profivá kell válni, és a TDD szemléletet gyakorlatba kell ültetni.
2.  **Microservices & Messaging:** A Spring Boot monolitikus alkalmazásokon túllépve meg kell ismerni a Microservice kommunikációs mintákat, API Gateway-eket, és az aszinkron üzenetküldést (Apache Kafka).
3.  **Adatbázisok (Advanced Hibernate):** Az adatbázis kapcsolatok, tranzakciókezelés, és lekérdezés optimalizálás mélyebb megértése (N+1 probléma, caching).
4.  **Cloud és Szoftverbiztonság:** Alapvető Cloud (AWS) ismeretek, illetve a webes sérülékenységek (OWASP Top 10) és a Spring Security (OAuth2, JWT) gyakorlati alkalmazása.
5.  **Frontend Alapok (JS/TS + React):** Bár a fókusz a backend, a modern JavaScript (ES6+), a TypeScript alapok és a REST API-k kliensoldali bekötésének (React) megértése manapság elengedhetetlen egy modern Java fejlesztőnek.

## 4. Tanulási Tervezet - Update 2.0 (A sikeres alapozás után)
**Jelenlegi státusz:** A projekt "Distributed Monolith" fázisban van. A Spring Boot, React, Security (OWASP Top 10) és a Terraform alapok sikeresen implementálva lettek.
**Új Időkeret:** A következő 1 hónap fókusza a "Cloud-Native, Event-Driven Microservice" végállapot elérése.

### 1. Fázis: Aszinkron Kommunikáció (Event-Driven Architecture)
*   **Fókusz:** Szolgáltatások szétválasztása és laza csatolása (Loose Coupling), mivel a hálózati routingot a felhő (K8s/Cloud Run) úgyis megoldja.
*   **Tanulandó:** Apache Kafka (vagy RabbitMQ) alapok, Eventual Consistency.
*   **Gyakorlat:** Kafka broker indítása Dockerben. Amikor új személy kerül mentésre a `DatabaseServer`-en, a rendszer dobjon egy eseményt (pl. `PersonCreatedEvent`), amit egy új, harmadik microservice (pl. `NotificationService`) aszinkron feldolgoz.

### 2. Fázis: Típusbiztosság és Modern Frontend (TypeScript)
*   **Fókusz:** A frontend robusztusságának növelése, ahogy azt a piac (pl. svájci cégek) elvárja.
*   **Tanulandó:** TypeScript integráció React-be, Zustand vagy Redux Toolkit állapotkezelés.
*   **Gyakorlat:** A jelenlegi JS alapú `UserInputServer` refaktorálása TypeScript-re. Interface-ek definiálása a backend DTO-khoz.

### 3. Fázis: Megbízhatóság és Teljesítmény (Resilience & Caching)
*   **Fókusz:** Mi történik, ha egy szolgáltatás leáll vagy belassul?
*   **Tanulandó:** Resilience4j (Circuit Breaker, Retry), Redis Caching, N+1 probléma javítása.
*   **Gyakorlat:** Keresési eredmények gyorsítótárazása Redis-ben. Hibatűrő fallback logikák írása, ha a `DatabaseServer` nem válaszol.

### 4. Fázis: Megfigyelhetőség (Observability)
*   **Fókusz:** A mikroszolgáltatások átláthatósága éles (Cloud) környezetben.
*   **Tanulandó:** Spring Boot Actuator, Prometheus (metrikák), Grafana (vizualizáció).
*   **Gyakorlat:** Docker Compose kibővítése Prometheusszal és Grafanával. Létrehozni egy műszerfalat, ami mutatja a CPU/Memória használatot és a HTTP kérések hibaarányát.

### 5. Fázis: Az utolsó szint - CI/CD és Cloud Deploy
*   **Fókusz:** Az automatizáció és az Infrastructure as Code (IaC) befejezése.
*   **Tanulandó:** GitHub Actions, Terraform véglegesítés.
*   **Gyakorlat:** Olyan GitHub Action írása, ami kódmódosításra lefut (tesztel, buildel). A korábban megírt `6.Cloud` Terraform kód élesítése az új, publikált Docker image-ekkel.
