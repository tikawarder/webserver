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

4, Frontend/React basics.                         - develope an easy ui with html, css and javascript.
                                                  - start some basic React to learn

5, Spring Boot                                    - Implement the above topics into Spring Boot. Use Spring MVC and other functions.

6, Google Cloud Provider (GCP)                    - Move the project to GCP

Preparing:
1, use java 17
2, install Docker CLI
3, Git has to be installed

Start:

1, Clone this repository

2, type ./start.sh to start the deployment process

    - it will start 3 containers (Mysql database, UserInputServer, DatabaseServer)
    - starts the UserInputServer with the built-in nginx server

3, visit localhost:8080

4, fill the input form, where the data acceptance will be checked by javascript and later sanitized by the server

5, then new person with its data will be sent to the DataBaseServer with Rest API

6, the Server receives the data and persist to the Mysql database

7, other direction of data happens when the UserInputServer fetches persons from the DatabaseServer

-----

8, to run the React developer server go to UserInputServer folder and type this: 
npm start

9, You can check the developer React app here: localhost:3000

10, Spring next tasks:
1. Validation
2. Global Exception Handling
3. Data Transfer Object
4. Pagination & Sorting
5. Unit & Integration Testing
6. Swagger / OpenAPI
----
7. Spring Boot Actuator
8. Spring Profiles
9. @Scheduled
10. Spring Events
11. Caching (@Cacheable)

11, How to deploy your codes to the Google Cloud (GCP):
1. Sign in to cloud console with google credentials, add billing method, create a project.
2. Install and setup gcloud to be able to create instances from your terminal
3. Create a virtual instance on GCP with command: 
   gcloud compute instances create database-server \
   --zone=us-east1-b \
   --machine-type=e2-micro \
   --image-family=debian-11 \
   --image-project=debian-cloud \
   --tags=mysql-server
4. Open firewall on 3306 port:
   gcloud compute firewall-rules create allow-mysql-access \
   --direction=INGRESS \
   --priority=1000 \
   --network=default \
   --action=ALLOW \
   --rules=tcp:3306 \
   --source-ranges={IP Address Range, or your IP}/32
5. Step in with SSH command to install the required software: 
   gcloud compute ssh database-server --zone=us-east1-b
6. Install docker here: sudo apt-get update
   sudo apt-get install -y docker.io
   sudo systemctl start docker
   sudo systemctl enable docker
7. Lets docker create a mysql container and run: sudo docker run -d \
   --name mysql-container \
   -p 3306:3306 \
   -e MYSQL_ROOT_PASSWORD=rootPassword \
   -v mysql_data:/var/lib/mysql \
   --restart always \
   mysql:latest
8. Install the docker containers into Cloud run. Use these commands where the Dockerfile is:
   gcloud run deploy backend-service \
   --image=us-east1-docker.pkg.dev/$PROJECT_ID/my-repo/backend \
   --region=us-east1 \
   --platform=managed \
   --allow-unauthenticated
9. Open the application frontend at: https://frontend-react-801953368913.us-east1.run.app

ssh coppmand to mysql-server: gcloud compute ssh mysql-server --tunnel-through-iap