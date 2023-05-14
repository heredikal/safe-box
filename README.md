# Safe-Box OpenAPI Specification

This repository contains the OpenAPI specification for the Safe-Box authorization component API. The API provides endpoints to open and manage a safe box, as well as access to the items stored in it.

## Usage

To use this API, follow these steps:

1. Clone or download this repository to your local machine.
2. Install an OpenAPI compliant client such as Swagger UI or Postman.
3. Use the endpoints provided by the API to open,
4. Insert the token into swagger authentication
5. Use the endpoints provided by the API to manage and access items in your vault.

## API Documentation

The Safe-Box OpenAPI specification defines the following endpoints:

### POST /safe-box/open

Opens a safe box for a user.

#### Query Parameters

- `username` (string, required): The username of the user. (into )
- `password` (string, required): The password of the user.  
(inside the properties file in the absence of a user control tool)
#### Response

- `200 OK`: The safe box was opened successfully. Returns a string containing the access token.

### GET /safe-box/items

Gets all items stored in the safe box.

#### Response

- `200 OK`: The items were retrieved successfully. Returns an array of `SafeBoxDTO` objects.

### POST /safe-box/items

Adds an item to the safe box.

#### Request Body

- `SafeBoxDTO` (object, required): The item to add to the safe box.

#### Response

- `200 OK`: The item was added to the safe box successfully.

## Authentication

All endpoints in this API require an `Authorization` header with a valid Token (uuid) in the format `Bearer {uuid}`. The uuid can be obtained by opening a safe box with the `POST /safe-box/open` endpoint.

## Security

This API uses the `bearerAuth` security scheme to authenticate requests. The `bearerAuth` scheme requires a uuid to be included in the `Authorization` header of each request.

## Dependencies

This API was developed using OpenAPI 3.0.1 and the following dependencies:

The project uses Java version 17 and has the following dependencies:

Spring Context 5.3.9  
Spring Boot Starter Web 2.5.0  
Spring Boot Starter Test 2.5.0  
Spring Boot Starter Data JPA 2.6.0  
Spring Data Commons 2.5.3  
springfox-swagger2 2.9.2  
springfox-swagger-ui 2.9.2  
H2 Database 1.4.200  
Lombok 1.18.20  
Java Persistence API 2.2  
JAXB API 2.3.1  
Jackson Annotations 2.13.0  
Spring Data JPA 2.5.2  
ModelMapper 2.4.4  
EasyRandom Core 5.0.0  
Commons IO 2.6  
JUnit Jupiter Engine 5.9.3  
JUnit Jupiter API 5.9.3  
JUnit Jupiter Params 5.9.3  
JUnit Platform Suite 1.7.1  

### Some of the notable dependencies include:

- Spring Boot: a popular framework for building web applications using Spring and Spring MVC.  
- H2 Database: a lightweight, in-memory database that can be used for testing and development purposes.  
- Lombok: a Java library that helps to reduce boilerplate code by generating getters, setters, constructors, and other common methods automatically.  
- ModelMapper: a Java library that simplifies mapping objects to other objects.  
- EasyRandom: a Java library for generating random test data.
- JUnit: a popular testing framework for Java applications.

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.