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

extra: Learn vulnerabilities                      - add a commit that introduce a security issue
                                                  - on the second commit, solve it

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

3, visit localhost:8080/webapp

4, then new person with its data will be sent to the DataBaseServer with Rest API

5, the Server receives the data and persist to the Mysql database

6, other direction of data happens when the UserInputServer fetches persons from the DatabaseServer

## 🔒 Vulnerabilities (Security Practice)

This branch focuses on introducing and professionally resolving common web vulnerabilities (OWASP Top 10).

| Vulnerability Type | Description & How to test | Status |
| :--- | :--- | :--- |
| **STORED-XSS** | Fill the input form with an executable script: `<script>alert("I got your cookies: " + document.cookie);</script>` | ✅ Fixed (via `InputSanitizer`) |
| **SQL Injection** | Go to `/search.jsp` and input `' OR 1=1 OR name LIKE '` instead of a name. It returns all persons. | ✅ Fixed (via Parameterized JPA) |
| **Broken Access Control** | The page `localhost:8080/webserver/search.jsp` can be accessed without logging in. | ✅ Fixed (via `SecurityFilter`) |
| **SSRF** | Server-Side Request Forgery in `AvatarFetchServlet`. User-supplied URLs are fetched directly without validation. | ✅ Fixed (via URL/IP Validation) |
| **Vulnerable Dependencies** | Using outdated third-party dependencies with known CVEs. | ⏳ Planned |
| **CSRF** | Cross-Site Request Forgery (testing scenarios with and without CSRF tokens). | ✅ Fixed (via `SecurityFilter`) |
| **IDOR** | Insecure Direct Object References. Modifying object IDs to access other users' protected data. | ✅ Fixed (via `SecurityFilter`) |
