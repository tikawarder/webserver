git pull
cd JavaServletApi
mvn clean package
docker stop simple-servlet-container
docker rm simple-servlet-container
docker build -t simple-servlet-app .
docker run --name simple-servlet-container -p 8080:8080 -d simple-servlet-app