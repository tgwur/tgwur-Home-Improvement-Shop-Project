
---

## Run Application


1. Update application.properties datasource, change the database url, username and password.
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=123456
spring.datasource.driver-class-name=org.postgresql.Driver
```
2. Use maven to compile project
```shell
mvn install
```
3. Install Lombok Plugin
4. Run StoreApplication.java to starting the application.
5. After starting the application, database schema was created, and we need add a record in the table system_user as a admin user, and set this admin's id to 0.
6. Open browser (recommend Edge) and access localhost:8888, select the manager role, use the admin username and password that we created in step3 to login.

---
