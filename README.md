
# HuddleApp – Part 3

## Project Overview
HuddleApp is a Java messaging application developed using Maven in NetBeans.

## Part 3 Features
The application supports:
- Sending messages.
- Disregarding messages.
- Storing messages in a JSON file.
- Generating unique message IDs.
- Creating message hashes.
- Displaying stored messages.
- Finding the longest stored message.
- Searching messages by message ID.
- Searching stored messages by recipient.
- Deleting stored messages using their message hash.
- Generating a report of stored messages.

## Message Arrays
The application uses arrays to manage:
- Sent Messages
- Disregarded Messages
- Stored Messages
- Message Hashes
- Message IDs

## Testing
JUnit tests are included to test the messaging functionality and Part 3 requirements.

## Technologies
- Java
- Apache Maven
- JUnit 4
- NetBeans IDE
- JSON file storage

## Running the Application
Open the project in NetBeans and run the Main.java file.

To run the tests, use the Maven test command:

    mvn test
