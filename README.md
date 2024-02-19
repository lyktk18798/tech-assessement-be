# URL Shortener Spring Boot App

This is a simple URL shortener application built using Spring Boot. It allows users to shorten long URLs into unique, easy-to-share links.

## Prerequisites

- Java Development Kit (JDK) 11 or higher
- Apache Maven
- Docker

## Getting Started

1. Clone this repository: https://github.com/lyktk18798/tech-assessement-be.git
2. Navigate to the project directory: ``cd tech-assessement-be``
3. Build the project:  ``./mvnw clean package ``
4. Run the application using Docker Compose: ``docker compose up``
5. Access the application at ``http://localhost:8080``.

## Configuration

- The application is configured to use Redis and MongoDB containers via Docker Compose. You can configure additional settings in the `application.properties` file.

## Technologies Used

- Spring Boot JPA
- Redis (containerized)
- MongoDB (containerized)
- Docker
- Docker Compose

## URL Shortener APIs

This API allows users to shorten long URLs into unique, easy-to-share links.

### Endpoints

#### Get Shortened URL Redirect

- **URL**: `GET /{id}`
- **Description**: Redirects the user to the original URL associated with the provided ID.
- **Parameters**:
    - `id` (path): The unique identifier for the shortened URL.
- **Response**:
    - If the ID exists and corresponds to a shortened URL, the user will be redirected to the original URL with status code 301 (Moved Permanently).
    - If the ID does not exist, a 404 error will be returned.

##### Example Usage Postman curl

``curl --location 'http://localhost:8080/468ecd'``

#### Create Shortened URL

- **URL**: `POST /create`
- **Description**: Creates a shortened URL for the provided long URL.
- **Request Body**:
    - `url` (string): The long URL to be shortened.
- **Response**:
    - If the provided URL is valid and successfully shortened, the API will return the shortened URL.
    - If the provided URL is invalid, a 400 error will be returned.

##### Example Usage Postman curl

``curl --location 'http://localhost:8080/create' \
--header 'Content-Type: text/plain' \
--data 'https://chir.ag/projects/name-that-color/#6195ED'``


## Running Unit Tests

To run unit tests for the Spring Boot application, execute the following command: ``./mvnw test``

## Usage

- Enter a long URL in the provided input field and click "Create a link".
- Copy the shortened URL and share it as needed.

There is a React App repository, check out here: https://github.com/lyktk18798/tech-assessment

## Contributing

Contributions are welcome! Please fork this repository and submit a pull request with your changes.