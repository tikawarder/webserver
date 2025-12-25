mvn -f DatabaseServer/pom.xml clean package
docker compose -f docker/docker-compose.yml up --build