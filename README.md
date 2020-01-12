# CinemaTicketSale
CinemaTicketSale is maven multi-module project written in Java 13 using JDBI 3 library for MySQL connection.
It simulates ticket sale at the cinema. User that is executing the application has option to sign in or create new account.
User may have role USER or ADMIN.

USER role functionalities:
* Buy new ticket for any movie that has been released.
* Search for movies that tickets can be bought on.
* Search for movie by name.
* Show bought ticket history which is also send to user email.

ADMIN role functionalities:
* Managing user:
    * Delete user from database
    * Update user details
    * Search for users
    * List all users
    * Give ADMIN role for users
    * Sort users in chosen order
    * Search users by name
    * Search for users by age
    * Search for users with valid loyalty card.
* Managing movies:
    * Add, delete or update movie
    * Search for movie 
    * List all movies
    * Sort movies
    * Group movies by genre
    * Search for movies in price range
    * Show movies to be released in given date range
* Search sales history:
    * Filter ticket sale history
    * Send full history on given e-mail
* Statistics options
    
## Installation
```
    mvn clean install
    cd main
    mvn clean compile assembly::single 
```
## Usage 
```
    cd target
    java --enable-preview -cp main-1.0-SNAPSHOT-jar-with-dependencies.jar stefanowicz.kacper.main.App
```
