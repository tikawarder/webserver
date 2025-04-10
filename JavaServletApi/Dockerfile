FROM tomcat:10.1.40-jdk17

# Töröljük az alapértelmezett alkalmazásokat (opcionális)
RUN rm -rf /usr/local/tomcat/webapps/*

# Bemásoljuk a saját WAR fájlt
COPY target/simple-servlet-app.war /usr/local/tomcat/webapps/

EXPOSE 8080

CMD ["catalina.sh", "run"]
