mvn -f DatabaseServer/pom.xml clean package
docker compose -f docker-compose.yml up --build