
package assignment.poe1;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("==============================");
        System.out.println("      Welcome to HuddleApp");
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

        Login user = new Login(
                username,
                password,
                cellphonenumber,
                firstname,
                lastname
        );

        System.out.println("\nRegistration Status:");
        System.out.println(user.registerUser());

        if (user.checkUserName()
                && user.checkPasswordComplexity()
                && user.checkCellPhoneNumber()) {

            System.out.println("\n==============================");
            System.out.println("            Login");
            System.out.println("==============================");

            System.out.println("Enter your username:");
            String loginUsername = scanner.nextLine();

            System.out.println("Enter your password:");
            String loginPassword = scanner.nextLine();

            boolean loginStatus =
                    user.loginUser(loginUsername, loginPassword);

            System.out.println(user.returnLoginStatus(loginStatus));

            if (loginStatus) {
                quickChat(scanner);
            }

        } else {
            System.out.println(
                    "Please restart the program and enter valid registration details."
            );
        }

        scanner.close();
    }

    private static void quickChat(Scanner scanner) {

        System.out.println("\nWelcome to QuickChat!");

        int messageLimit;

        while (true) {

            System.out.println(
                    "How many messages would you like to enter?"
            );

            String input = scanner.nextLine().trim();

            try {
                messageLimit = Integer.parseInt(input);

                if (messageLimit >= 0) {
                    break;
                }

            } catch (NumberFormatException e) {
                // Handle invalid input below.
            }

            System.out.println(
                    "Please enter a whole number that is zero or greater."
            );
        }

        int processed = 0;
        int messageNumber = 0;

        while (true) {

            System.out.println("\n========== QuickChat Menu ==========");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("Choose an option:");

            String option = scanner.nextLine().trim();

            switch (option) {

                case "1":

                    if (processed >= messageLimit) {
                        System.out.println(
                                "You have reached your message limit."
                        );
                        break;
                    }

                    System.out.println("Enter recipient cell number:");
                    String recipient = scanner.nextLine().trim();

                    System.out.println("Enter your message:");
                    String text = scanner.nextLine();

                    Message message = new Message(
                            messageNumber,
                            recipient,
                            text
                    );

                    System.out.println(
                            "Message ID generated: "
                            + message.getMessageID()
                    );

                    String recipientResult =
                            message.checkRecipientCell();

                    if (!recipientResult.equals(
                            "Cell phone number successfully captured.")) {

                        System.out.println(recipientResult);
                        System.out.println(
                                "Please return to option 1 and try again."
                        );
                        break;
                    }

                    String lengthResult =
                            message.validateMessageLength();

                    if (!lengthResult.equals("Message ready to send.")) {
                        System.out.println(lengthResult);
                        System.out.println(
                                "Please return to option 1 and try again."
                        );
                        break;
                    }

                    System.out.println(lengthResult);

                    System.out.println(
                            "Message Hash: " + message.createMessageHash()
                    );

                    System.out.println("\nChoose what to do with the message:");
                    System.out.println("1) Send Message");
                    System.out.println("2) Disregard Message");
                    System.out.println("3) Store Message to send later");
                    System.out.println("Choose an option:");

                    String action = scanner.nextLine().trim();

                    if (!action.equals("1")
                            && !action.equals("2")
                            && !action.equals("3")) {

                        System.out.println(
                                "Invalid option. Message was not processed."
                        );
                        break;
                    }

                    String result = message.SentMessage(action);
                    System.out.println(result);

                    if (action.equals("2")) {

                        System.out.println(
                                "Enter 0 to delete the message:"
                        );

                        String confirmation = scanner.nextLine().trim();

                        if (confirmation.equals("0")) {
                            System.out.println(
                                    message.disregardMessage()
                            );
                        } else {
                            System.out.println(
                                    "Message was not deleted."
                            );
                        }

                    } else if (action.equals("1")
                            && message.isSent()) {

                        System.out.println("\nMessage Details:");
                        System.out.println(message.toDisplayString());
                    }

                    if (action.equals("1")
                            || action.equals("2")
                            || action.equals("3")) {

                        processed++;
                        messageNumber++;
                    }

                    System.out.println(
                            "Messages processed: "
                            + processed + " of " + messageLimit
                    );

                    if (processed >= messageLimit) {
                        System.out.println(
                                "Total number of messages sent: "
                                + message.returnTotalMessages()
                        );
                    }

                    break;

                case "2":

                    String sentMessages = new Message(
                            0, "", ""
                    ).printMessages();

                    if (sentMessages.isEmpty()) {
                        System.out.println(
                                "No sent messages to display."
                        );
                    } else {
                        System.out.println("\nRecently Sent Messages:");
                        System.out.println(sentMessages);
                    }

                    break;

                case "3":

                    System.out.println("Goodbye.");
                    return;

                default:

                    System.out.println(
                            "Invalid option. Please select 1, 2, or 3."
                    );
                    break;
            }
        }
    }
}
