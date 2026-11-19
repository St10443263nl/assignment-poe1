
package assignment.poe1;

import java.io.IOException;
import java.util.Scanner;

// Main application for HuddleApp registration, login and QuickChat.
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("==============================");
        System.out.println("   Welcome to HuddleApp");
        System.out.println("==============================");

        System.out.println("Enter your first name:");
        String firstname = scanner.nextLine();

        System.out.println("Enter your last name:");
        String lastname = scanner.nextLine();

        System.out.println("Enter your username:");
        String username = scanner.nextLine();

        System.out.println("Enter your password:");
        String password = scanner.nextLine();

        System.out.println("Enter your cellphone number:");
        String cellphonenumber = scanner.nextLine();

        Login user = new Login(username, password, cellphonenumber,
                firstname, lastname);

        System.out.println("\nRegistration Status:");
        System.out.println(user.registerUser());

        if (user.checkUserName() && user.checkPasswordComplexity()
                && user.checkCellPhoneNumber()) {

            System.out.println("\n==============================");
            System.out.println("            Login");
            System.out.println("==============================");

            System.out.println("Enter your username:");
            String loginUsername = scanner.nextLine();

            System.out.println("Enter your password:");
            String loginPassword = scanner.nextLine();

            boolean loginStatus = user.loginUser(loginUsername, loginPassword);

            System.out.println(user.returnLoginStatus(loginStatus));

            if (loginStatus) {
                quickChat(scanner, firstname + " " + lastname);
            }
        }

        scanner.close();
    }

    private static void quickChat(Scanner scanner, String senderName) {
        System.out.println("Welcome to QuickChat.");

        try {
            Message.loadStoredMessages();
        } catch (IOException ex) {
            System.out.println("Stored messages could not be loaded: "
                    + ex.getMessage());
        }

        int messageLimit = readNonNegativeInteger(scanner,
                "How many messages would you like to enter?");

        int processed = 0;
        int messageNumber = 0;

        while (true) {
            System.out.println("\n1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
            System.out.println("Choose an option:");

            String option = scanner.nextLine().trim();

            switch (option) {
                case "1":
                    if (processed >= messageLimit) {
                        System.out.println("You have reached your message limit.");
                        break;
                    }

                    if (sendOneMessage(scanner, messageNumber)) {
                        processed++;
                        messageNumber++;
                    }

                    if (processed >= messageLimit) {
                        System.out.println("Total number of messages sent: "
                                + new Message(0, "", "").returnTotalMessages());
                    }
                    break;

                case "2":
                    String sentOutput =
                            new Message(0, "", "").printMessages();

                    System.out.println(sentOutput.isEmpty()
                            ? "No recently sent messages."
                            : sentOutput);
                    break;

                case "3":
                    System.out.println("Goodbye.");
                    return;

                case "4":
                    storedMessagesMenu(scanner, senderName);
                    break;

                default:
                    System.out.println(
                            "Invalid option. Please select 1, 2, 3 or 4.");
            }
        }
    }

    private static int readNonNegativeInteger(Scanner scanner, String prompt) {
        while (true) {
            System.out.println(prompt);
            String input = scanner.nextLine().trim();

            try {
                int value = Integer.parseInt(input);

                if (value >= 0) {
                    return value;
                }

            } catch (NumberFormatException ignored) {
                // Ask again if the input is not a whole number.
            }

            System.out.println(
                    "Please enter a whole number that is zero or greater.");
        }
    }

    private static boolean sendOneMessage(Scanner scanner, int messageNumber) {
        while (true) {
            System.out.println("Enter recipient cell number:");
            String recipient = scanner.nextLine().trim();

            System.out.println("Enter your message:");
            String text = scanner.nextLine();

            Message message = new Message(messageNumber, recipient, text);

            System.out.println("Message ID generated: "
                    + message.getMessageID());

            String recipientResult = message.checkRecipientCell();

            if (!recipientResult.equals(
                    "Cell phone number successfully captured.")) {

                System.out.println(recipientResult);
                System.out.println("Please try entering the message again.");
                continue;
            }

            String lengthResult = message.validateMessageLength();

            if (!lengthResult.equals("Message ready to send.")) {
                System.out.println(lengthResult);
                System.out.println("Please try entering the message again.");
                continue;
            }

            System.out.println(lengthResult);
            System.out.println("Message Hash: "
                    + message.createMessageHash());

            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");
            System.out.println("Choose an option:");

            String action = scanner.nextLine().trim();

            String result = message.SentMessage(action);
            System.out.println(result);

            if ("2".equals(action)) {
                System.out.println("Enter 0 to delete the message:");

                if ("0".equals(scanner.nextLine().trim())) {
                    System.out.println(message.disregardMessage());
                } else {
                    System.out.println("Message was not deleted.");
                }

            } else if ("1".equals(action) && message.isSent()) {
                System.out.println(message.toDisplayString());
            }

            return "1".equals(action)
                    || "2".equals(action)
                    || "3".equals(action);
        }
    }

    // Fourth main-menu option: Stored Messages.
    private static void storedMessagesMenu(Scanner scanner, String senderName) {

        while (true) {
            System.out.println("\n========== Stored Messages ==========");

            System.out.println(
                    "1) Display sender and recipient of all stored messages");

            System.out.println("2) Display the longest stored message");

            System.out.println(
                    "3) Search for a stored message by Message ID");

            System.out.println(
                    "4) Search for stored messages by recipient");

            System.out.println(
                    "5) Delete a stored message using its Message Hash");

            System.out.println(
                    "6) Display a full report of all stored messages");

            System.out.println("7) Return to main menu");

            System.out.println("Choose an option:");

            String choice = scanner.nextLine().trim();

            switch (choice) {

                // Function 1: Display sender and recipient.
                case "1":
                    Message[] stored = Message.getStoredMessages();

                    if (stored.length == 0) {
                        System.out.println("There are no stored messages.");

                    } else {
                        for (Message message : stored) {
                            System.out.println("Sender: " + senderName);
                            System.out.println("Recipient: "
                                    + message.getRecipient());
                            System.out.println();
                        }
                    }
                    break;

                // Function 2: Display the longest stored message.
                case "2":
                    Message longest = Message.getLongestStoredMessage();

                    System.out.println(longest == null
                            ? "There are no stored messages."
                            : longest.toDisplayString());
                    break;

                // Function 3: Search by Message ID.
                case "3":
                    System.out.println("Enter the Message ID:");

                    String id = scanner.nextLine().trim();

                    Message foundByID =
                            Message.searchStoredMessageByID(id);

                    System.out.println(foundByID == null
                            ? "Message not found."
                            : foundByID.toDisplayString());
                    break;

                // Function 4: Search by recipient.
                case "4":
                    System.out.println(
                            "Enter the recipient cell number:");

                    String recipient = scanner.nextLine().trim();

                    Message[] matches =
                            Message.searchStoredMessagesByRecipient(recipient);

                    if (matches.length == 0) {
                        System.out.println(
                                "No stored messages found for that recipient.");

                    } else {
                        for (Message message : matches) {
                            System.out.println(message.toDisplayString());
                            System.out.println();
                        }
                    }
                    break;

                // Function 5: Delete using Message Hash.
                case "5":
                    System.out.println(
                            "Enter the Message Hash of the message to delete:");

                    String hash = scanner.nextLine().trim();

                    try {
                        System.out.println(
                                Message.deleteStoredMessageByHash(hash)
                                ? "Stored message successfully deleted."
                                : "No stored message was found with that hash.");

                    } catch (IOException ex) {
                        System.out.println(
                                "Message could not be deleted: "
                                + ex.getMessage());
                    }
                    break;

                // Function 6: Display full stored-message report.
                case "6":
                    System.out.println(
                            Message.generateStoredMessagesReport());
                    break;

                // Return to the main menu.
                case "7":
                    return;

                default:
                    System.out.println(
                            "Invalid option. Please select 1 to 7.");
            }
        }
    }
}
