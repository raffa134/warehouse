# Warehouse API

This service is a Spring Boot application written in Kotlin. 
Following the MVC pattern, it provides a REST API to manage articles and products of the warehouse. 
Click [here](https://app.swaggerhub.com/apis/raffa134/warehouse-api/1.0.0) to check the API documentation.

Spring Boot takes away a lot of boilerplate code, making it easier to build production-grade applications while focusing on the business logic.
The application uses Hibernate to manipulate DB data and H2 as persistence layer.
Data Transfer Objects used by controllers are automatically generated based on the API contract via OpenAPI Generator Maven plugin.

The goal was to have an application "on its way to production", but due to time constraints some features are still missing:
* Additional endpoints for data manipulation (e.g. PUT/DELETE to update/delete articles and products)
* Profile specific configurations (e.g. different logging levels and security configurations per environment)
* Different DBMS (H2 should only be used for local profile)
* A DB migration tool (e.g. Flyway) to be able to apply version control to DB schemas
* Security configuration (SSL configuration and endpoint-specific permissions based on requirements)
* Monitoring (metrics are easily configurable via Spring Actuators) and alerting
* Functional tests and additional unit/integration tests
* CI/CD configuration 
 
In general, the application is designed and implemented based on assumptions which should be validated and revised against real use case scenarios.

### Prerequisites to run the application

Before being able to run this project please make sure you have the following packages installed:
- Git
- Java
- Maven

Alternatively you can run the application as a [docker container](#docker-container).

### Build and execute the project

#### Executable .jar file
In order to build the project and run the service, you need to execute the following commands from the project's root folder:

```bash
mvn clean install
mvn spring-boot:run
```

#### Docker Container
The application is also runnable as a Docker container. 
Run the following commands from the project's root folder in order to build the docker image and run it locally:

```bash
mvn clean install
docker build -t warehouse-application -f docker/Dockerfile .
docker run -p 8080:8080 warehouse-application
```

In case you don't have maven installed, you can directly pull the image from my docker hub repository:
```bash
docker run -p 8080:8080 raffa134/warehouse:1.0.0
```

### Test the application
You can test the application with Postman or curl. 
Click [here](https://app.swaggerhub.com/apis/raffa134/warehouse-api/1.0.0) to check the API documentation.

#### Postman
A Postman collection with the complete set of endpoints is available under `docs/postman-support`.

#### curl

You can run the following commands from the project's root folder to test the application via curl:

**Note:** You can of course replace the payloads with your own json files. 

##### `POST /articles`

```bash
curl -i -d @src/test/resources/jsonPayloads/correctPayloads/article.json -H "Content-Type: application/json" -X POST http://localhost:8080/articles

HTTP/1.1 201 
Content-Length: 0
```

##### `GET /articles`

```bash
curl -i http://localhost:8080/articles

HTTP/1.1 200 
Content-Type: application/json
{"inventory":[{"art_id":1,"name":"leg","stock":36}]}
```

##### `POST /articles/file`
```bash
curl -i -F file=@src/test/resources/jsonPayloads/correctPayloads/articlesInventory.json http://localhost:8080/articles/file

HTTP/1.1 201 
Content-Length: 0
```

##### `POST /products`

```bash
curl -i -d @src/test/resources/jsonPayloads/correctPayloads/product.json -H "Content-Type: application/json" -X POST http://localhost:8080/products

HTTP/1.1 201 
Content-Length: 0
```
##### `GET /products`

```bash
curl -i http://localhost:8080/products

HTTP/1.1 200 
Content-Type: application/json
{"products":[{"id":1,"name":"Dining Chair","is_available":true,"stock":4,"contain_articles":[{"art_id":1,"amount_of":4},{"art_id":2,"amount_of":8},{"art_id":3,"amount_of":1}]}]}
```

##### `POST /products/file`
```bash
curl -i -F file=@src/test/resources/jsonPayloads/correctPayloads/productsFile.json http://localhost:8080/products/file

HTTP/1.1 201 
Content-Length: 0
```

##### `POST /products/{id}/sell`
```bash
curl -i -X POST http://localhost:8080/products/1/sell

HTTP/1.1 200 
Content-Length: 0
```