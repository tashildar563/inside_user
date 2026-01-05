### GOALS

1. Implement a Spring Boot microservice responsible for user management.  
2. Implement another Spring Boot microservice responsible for journaling user events.

### REQUIREMENTS

User Service

* Include functionalities for user registration, user details retrieval, updating and deleting user information and managing user roles. (You can use any database to store the user details).  
* Expose RESTful API endpoints for CRUD operations.  
* Integrate Spring Security for role-based authentication.  
* Publish user events to a Kafka topic named “user-events” for journaling.


  
Journal Service

* Consume events from user-events Kafka topic, log a message and record them. (You can use any database to store the journals)  
* Implement RESTful API endpoints to retrieve journals.  
* Incorporate role-based access control to retrieve journals.

Good to have  
• Unit tests  
• Integration tests  
Documentation  
• Create docker compose file to run the entire system.  
• Document REST API endpoints.  
• Document how to run and test the entire system.

### 1 Post-Deployment Scripts:

### Create databases
```sql 
CREATE DATABASE journal; CREATE DATABASE user_db; —-Insert ROLE_ADMIN in db:�� INSERT INTO `user_db`.`role` � ( `is_deleted`, name`)� VALUES(FALSE,'ROLE_ADMIN');��---Insert Super user in db:�� INSERT INTO `user_db`.`user` � ( `email`, `first_name`, `is_deleted`, `last_name`, `password`, `role_id`)� values('superuser@gemail.com', 'superuser', FALSE, 'super', '$2a$10$RGQHNlWHsAwDKqOA5Z1y1OpdLx1vww8kwEql3TbzDuTVvHQMt1gwa`role`', � (SELECT id FROM `user_db`.`role` WHERE `name`='ADMIN'));
```

It is mandatory to first create the superuser using the provided script.

### 2 User Service functionalities:

#### **2.1 ER Diagram**


* **User** table includes a role\_id foreign key that links to the **Role** table's primary key.  
* This establishes a one-to-many relationship where one role can be associated with many users, but each user can only have one assigned role.

This structure simplifies role management by ensuring that each user is linked to a specific role, enabling efficient access control based on the defined roles in the application.

#### **2.2 Authentication of Authorized User**

* The administrator or authorized user logs into the system using their credentials (username and password).  
* The system verifies their identity and checks their role to ensure they have permission to create new users.  
* JWT (JSON Web Tokens) is used to authenticate and authorize administrators for creating new user accounts securely.

* Steps  
1. User Authentication:  
   * The administrator logs in with their credentials.  
   * On successful authentication, the server generates a JWT and sends it to the client.  
2. Token Storage:  
   * The client stores the JWT (e.g., in localStorage).  
3. Access User Creation Interface:  
   * The administrator navigates to the user management section.  
4. Create User Request:  
   * Upon filling out the user creation form, the client sends a request with the JWT in the Authorization header:  
     Authorization: Bearer \<JWT\>  
5. Server-side Token Validation:  
   * The server verifies the JWT’s validity and checks user permissions based on claims.  
6. Process User Creation:  
   * The server validates the input, hashes the password, and stores the new user in the database.

#### **2.3. User Registration:**  **User registration is the process through which new users create an account on a system or application. It typically involves collecting user information, validating it, and storing it securely in a database.**

##### 2.3.1 Access User Creation Interface

* Once authenticated, the authorized user navigates to the user registration section of the application, where they can create new user accounts.

#### **2.4 Update User Details**

The functionality to update user details allows users to modify their personal information within the application.  
Implementing the update user details functionality enhances user control over personal information and contributes to a better user experience

##### 2.4.1 Authentication Required

* Users must be logged in to update their details, typically using JWT for authentication.

#### **2.5 Delete User Details**  

Logically deleting a user involves marking the user as inactive rather than permanently removing their data from the database. This approach preserves user information for auditing and potential reactivation. Logically deleting user details is an essential feature for maintaining data integrity and compliance while providing flexibility in user management

#####  2.5.1 Authentication Required

* Users must be logged in to delete user details, typically using JWT for authentication.

#### **2.6 Create New Role** 	The functionality to create new roles allows administrators to define user permissions and access levels within an application.Implementing the functionality to create new roles is essential for flexible user management and access control in an application.

##### 2.6.1 Authentication Required

Users must be logged in to delete user details, typically using JWT for authentication.

#### **2.7 Delete Role** 	The functionality to delete roles allows administrators to manage user permissions effectively by removing roles that are no longer needed.Implementing the delete role functionality is crucial for effective role management and ensuring that your application remains secure and organised. 

##### 2.7.1 Authentication Required

* Users must be logged in to delete user details, typically using JWT for authentication.

### **3\. Journal Service**

	The Journal Service is designed to efficiently consume Kafka event messages, process them, and store them as journal entries. This service provides a robust mechanism for logging events and allows users to retrieve these entries via a REST API.

#### **3.1 Key Features**

1. Kafka Event Consumption:  
   * The service listens to ‘user\_event’ Kafka topics to consume incoming event messages in real-time. This allows for seamless integration with other systems and applications that produce events.  
2. Journal Entry Storage:  
   * Each consumed event is processed and stored as a journal entry in a database. This structured storage enables efficient querying and retrieval of event data.

#### **3.2 ER Diagram:**

### **4\. Document how to run and test the entire system.**

1. **Bring the Service Online**:  
* Start the service to make it operational.  
2. **Execute Post-Deployment Script**:  
* Run the post-deployment script to create the database and insert the superuser details to initialise the system.  
3. **Open Postman**:  
* Launch Postman to test the APIs.

#### **4.1 Login to the System:**

* After executing the post-deployment script, use the "login" API to authenticate into the system.

| POST http://localhost:8080/inside\_user/authentication/login//request{    "email": "john.doe@example.com",    "password": "password123"}//response{    "id": 2,    "email": "john.doe@example.com",    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huLmRvZUBleGFtcGxlLmNvbSIsInJvbGVzIjpbIlJPTEVfQURNSU4iXSwiaWF0IjoxNzI2OTMxMzE0LCJleHAiOjE3MjY5MzQ5MTR9.p7Y5I7ZLd30zcgaxkfs66WyEgABRW977Ohe8TQKuKZw",    "type": "Bearer",    "roles": \[        "ROLE\_ADMIN"    \]} |
| :---- |

#### **4.2 create a new user :**  **After logging into the system, follow these steps to create a new user using the API:**

1. **Obtain and Save the Token**:  
   * The token must be generated from a user with the ROLE\_ADMIN role. Log in using the credentials of an admin user to receive the token.

2. **Prepare the Register User API Request**:  
   * Use the following cURL command to execute the register-user API. Replace \<token\> with the copied token and fill in the user details as needed.

**Role Assignment**:

* Ensure that the role you assign to the new user exists and that you have the necessary permissions to assign it. If the role is invalid or not accessible, the user creation process will fail.

| curl \--location 'http://localhost:8080/inside\_user/authentication/register-user' \\\--header 'Authorization: Bearer \<token\>' \\\--header 'Content-Type: application/json' \\\--data-raw '{    "firstName": "John11",    "lastName": "Doe",    "email": "john11.doe@example.com",    "password": "password123",    "role":"ROLE\_ADMIN"}' |
| :---- |

![][image1]

**Admin Role Requirement:**

* Only users with the ROLE\_ADMIN can create new users. Make sure the authenticated user has this role; otherwise, the request will be denied.

If the register-user API execution succeeds, you will receive a response similar to the following:

| {    "id": 14,    "status": "User with email: john11.doe@example.com Registered.",    "email": "john11.doe@example.com",    "role": "ROLE\_ADMIN",    "lastName": "Doe",    "firstName": "John11"} |
| :---- |

#### **4.3 Update User : After logging into the system, follow these steps to update user using the API::**

3. **Obtain and Save the Token**:  
   * The token must be generated from a user with the ROLE\_ADMIN role. Log in using the credentials of an admin user to receive the token.  
4. **Prepare the Register User API Request**:

Use the following cURL command to execute the update API. Replace \<token\> with the copied token and fill in the user details as needed. And also replace the {id} with user id whose details has to be updated.

| curl \--location 'http://localhost:8080/inside\_user/authentication/update' \\\--header 'Authorization: Bearer \<token\>' \\\--header 'Content-Type: application/json' \\\--data-raw '{    "id":"{id}",    "firstName": "John100",    "lastName": "joe",    "email": "john10.doe@example.com",    "password": "password123",    "role":"ROLE\_ADMIN"}' |
| :---- |

If the update API execution succeeds, you will receive a response similar to the following:

**{**  
    **"message": "User with email: john10.doe@example.com updated.",**  
    **"id": 13,**  
    **"email": "john10.doe@example.com"**  
**}**

#### **4.4 Delete user : After logging into the system, follow these steps to delete user using the API::**

5. **Obtain and Save the Token**:  
   * The token must be generated from a user with the ROLE\_ADMIN role. Log in using the credentials of an admin user to receive the token.  
6. **Prepare the Register User API Request**:

Use the following cURL command to execute the delete API. Replace \<token\> with the copied token and fill in the user details as needed. And also replace the {id} with user id whose details has to be updated.

| curl \--location \--request POST 'http://localhost:8080/inside\_user/authentication/delete/{id}' \\\--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdXBlcnVzZXJAZ2VtYWlsLmNvbSIsInJvbGVzIjpbIlJPTEVfQURNSU4iXSwiaWF0IjoxNzI3MDA5NzQ2LCJleHAiOjE3MjcwMTMzNDZ9.AaAjGljh2HT2-FXcz9LYC2k8y8kdnSgg0niswbMzQw4' \\\--data '' |
| :---- |

If the delete API execution succeeds, you will receive a response similar to the following:

| {    "message": "User with email: john10.doe@example.com deleted.",    "id": 13,    "email": "john10.doe@example.com"} |
| :---- |

#### **4.5 retrieve all user : After logging into the system, follow these steps to retrieve all user using the API:**

7. **Obtain and Save the Token**:  
   * The token must be generated from a user with the ROLE\_ADMIN or ROLE\_USER\_ONE role. Log in using the credentials of an admin user to receive the token.  
8. **Prepare the Register User API Request**:

Use the following cURL command to execute the all API. Replace \<token\> with the copied token and fill in the user details as needed.

| curl \--location 'http://localhost:8080/inside\_user/users/all' \\\--header 'Authorization: Bearer \<token\>' \\\--data '' If the all API execution succeeds, you will receive a response similar to the following:\[    {        "id": 2,        "firstName": "superuser",        "lastName": "Doe",        "email": "superuser@gemail.com",        "role": "ROLE\_ADMIN"    },    {        "id": 3,        "firstName": "John",        "lastName": "Doe",        "email": "john.doe@example.com",        "role": "ROLE\_ADMIN"    },    {        "id": 4,        "firstName": "John1",        "lastName": "Doe",        "email": "john1.doe@example.com",        "role": "ROLE\_USER\_ONE"    },    {        "id": 5,        "firstName": "John2",        "lastName": "Doe",        "email": "john2.doe@example.com",        "role": "ROLE\_USER\_ONE"    }\] |
| :---- |

#### **4.6 create-new-role : After logging into the system, follow these steps to create-new-role for user using the API::**

9. **Obtain and Save the Token**:  
   * The token must be generated from a user with the ROLE\_ADMIN role. Log in using the credentials of an admin user to receive the token.  
10. **Prepare the Register User API Request**:

Use the following cURL command to execute the create-new-role API. Replace \<token\> with the copied token and fill in the user details as needed.

| curl \--location 'http://localhost:8080/inside\_user/roles/create-new-role' \\\--header 'Authorization: Bearer \<token\>' \\\--header 'Content-Type: application/json' \\\--data '{    "name": "TEST1",    "description":"First test"}' |
| :---- |

If the create-new-role API execution succeeds, you will receive a response similar to the following:

| {    "id": 6,    "name": "ROLE\_TEST1",    "description": "First test",    "status": "New role created successfully\!"} |
| :---- |

#### **4.7 delete user role: After logging into the system, follow these steps to delete user role  using the API::**

11. **Obtain and Save the Token**:  
    * The token must be generated from a user with the ROLE\_ADMIN role. Log in using the credentials of an admin user to receive the token.  
12. **Prepare the Register User API Request**:

Use the following cURL command to execute the delete API. Replace \<token\> with the copied token and fill in the user details as needed.

| curl \--location \--request DELETE 'http://localhost:8080/inside\_user/roles/delete/TEST1' \\\--header 'Authorization: Bearer \<token\>' \\\--data '' |
| :---- |

If the delete API execution succeeds, you will receive a response similar to the following:

| {    "id": 6,    "name": "ROLE\_TEST1",    "description": null,    "status": "Role deleted successfully\!"} |
| :---- |

#### **4.8 Retrieve all roles: After logging into the system, follow these steps to retrieve all roles using the API:**

13. **Obtain and Save the Token**:  
    * The token must be generated from a user with any role. Log in using the credentials of an admin user to receive the token.  
14. **Prepare the Register User API Request**:

Use the following cURL command to execute the all-roles API. Replace \<token\> with the copied token and fill in the user details as needed.

| curl \--location 'http://localhost:8080/inside\_user/roles/all-roles' \\\--header 'Authorization: Bearer \<token\>' \\\--data '' |
| :---- |

If the all-roles API execution succeeds, you will receive a response similar to the following:

| \[    {        "id": 1,        "name": "ROLE\_ADMIN",        "description": null,        "status": null    },    {        "id": 3,        "name": "ROLE\_USER\_ONE",        "description": null,        "status": null    }\] |
| :---- |

#### **4.9 Get journal entries : After logging into the system, follow these steps to get journal entries using the API:**

15. **Obtain and Save the Token**:  
    * The token must be generated from a user with any role. Log in using the credentials of an admin user to receive the token.  
16. **Prepare the Register User API Request**:  
    * Use the following cURL command to execute the journal API. Replace \<token\> with the copied token and fill in the user details as needed.

| http://localhost:8080/inside\_user/users/journal//request{    "event":"User Registration"} |
| :---- |

| //response\[    {        "data": {            "id": "4",            "event": "User Registration",            "message": "User with email: john1.doe@example.comRegistered."        },        "created\_on": "22-09-2024 06:42:55",        "id": "11",        "event": "User Registration",        "created\_by": "4"    },    {        "data": {            "id": "5",            "event": "User Registration",            "message": "User with email: john2.doe@example.comRegistered."        },        "created\_on": "22-09-2024 11:44:10",        "id": "34",        "event": "User Registration",        "created\_by": "5"    }\] |
| :---- |

[image1]: <data:image/png;base64,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>
