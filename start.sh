git pull
cd DatabaseServer
mvn clean package
cd ..
cd UserInputServer
mvn clean package
cd ..
cd docker
docker compose up --build