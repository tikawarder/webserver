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
3, Git has to be installed

Start:

1, Clone this repository

2, type ./start.sh to start the deploy process

    - it will start 3 containers (Mysql database, UserInputServer, DatabaseServer)
    - starts the UserInputServer with the built-in tomcat server

3, visit localhost:8080/webapp/

4, fill the input form, where the data acceptance will be checked by javascript and later sanitized by the server

5, then person object will be sent to the DataBaseServer with Rest API

6, the Server receives the data and persist to the Mysql database

7, the other direction of data happens when the UserInputServer asks all persons from the DatabaseServer

-----

to run the React developer server go to UserInputServer/React/my-new-react-app and type this: 
npm start

You can check the new developer React app here: localhost:3000