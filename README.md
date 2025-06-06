The study plan (agreed with Adorjan)
- To be able to tell "what is web service".
- REST web services (jax-rs)
  - SOAP as second hand :-(
- Plan an app that uses web service. It can receive and send REST messages.
- Implement that app.
- Do the same with Spring Boot
- Do the same in Cloud world (GCP or other)
  
My detailed plan with milestones:

1, Servlets                                       - Create servlets with jsp's that communicate with each other

2, Forms and user inputs. Store in Database.      - Use a form on the UI and get data from user. Store these data.
                                                  - Get data from Database with different SQL commands.

3, REST and SOAP communication.                   - Just 1 simple trial for SOAP.
                                                  - Implement an app with REST (app and database communication). Use MVC.

4, Frontend basics.                               - develope an easy ui with html, css and javascript.
                                                  - start some basic React to learn

5, Spring Boot                                    - Implement the above topics into Spring Boot. Use Spring MVC and other functions.

6, Google Cloud Privider (GCP)                    - Move the project to GCP

Preparing:
1, use java 17
2, install Docker CLI
3, Git installed

Start:

1, Clone this repository

2, type ./start.sh to start the deploy process

    - it will start a mysql docker container
    - deploys the java code with a built-in tomcat server

3, visit localhost:8080/webapp/

4, When Soap server (DatabaseServer) is started, the endpoint is that: http://database-server:8081/ws/decoder?wsdl

5, Soap client classes (in UserInputServer) can be generate with in the beginning with /wsimport/wsimport.sh
    usage: from UserInputServer root:  /wsimport/wsimport.sh -s /UserInputServer/src/main/java -p soapclient http://localhost:8081/ws/decoder?wsdl
