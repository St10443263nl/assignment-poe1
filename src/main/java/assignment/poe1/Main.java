package assignment.poe1;
//Main application for HuddleApp registration and login
//Uses scanner to collect user registration and login details

import java.util.Scanner;

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

        Login user = new Login(
                username,
                password,
                cellphonenumber,
                firstname,
                lastname
        );

        // Display registration result
        System.out.println("\nRegistration Status:");
        System.out.println(user.registerUser());

        // Only continue to  if all registration details are correct
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
        }

        scanner.close();
    }
}