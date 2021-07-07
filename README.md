# Precis

Precis is simple service to create shortened urls and using them to redirect to long urls

## Requirements:

The project is based on Spring Boot and Hibernate and is using MySql database
Gradle is used as a build tool
To run the project:
-Open the project with Eclipse/Intellij
-Make sure you have a MySql database running. Also mention the database config in application.yml file.
-Run PrecisApplication as a spring boot application


## Application Flow:

![precis-flow](https://user-images.githubusercontent.com/47517129/124497262-3530d800-ddd8-11eb-92f7-9c0b14621a7b.jpg)

## Backend Info:

We have exposed two REST APIs for shortening the url and one to get longUrl from database
-localhost:8080/app/rest/shorten
-localhost:8080/app/rest/getLong

## Logic to calculate shortUrl

Using the longURL, I am getting a hashed value (using SHA256) and encoding it using Base64 and using the first 8 characters as a string for shortUrl
