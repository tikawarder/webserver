docker run --name mysql_for_webserver -p 3307:3306 -d -e MYSQL_ROOT_PASSWORD=root mysql:8.0 //password: root
git pull
cd JavaServletApi
mvn clean package tomcat7:run