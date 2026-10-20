package assignment.poe1;
//Handles user registration and login
import java.util.regex.Pattern;

// This class handles user login and validates login details.
public class Login {
    
private String username;
private String password;
private String cellphonenumber;
private String firstname;
private String lastname;

// Constructor
public Login(String username, String password, String cellphonenumber,String firstname, String lastname) {
    
this.username = username;
this.password = password;
this.cellphonenumber = cellphonenumber;
this.firstname = firstname;
this.lastname = lastname;
}
// Check if username contains an underscore and is no more than 5 characters
public boolean checkUserName() {
return username != null
&& username.contains("_")
&& username.length() <= 5;
}
// Check password complexity 8char
public boolean checkPasswordComplexity() {
if (password == null || password.length() < 8) {
return false;
}
boolean hasCapitalLetter = false;
boolean hasNumber = false;
boolean hasSpecialCharacter = false;
for (char c : password.toCharArray()) {
if (Character.isUpperCase(c)) {
hasCapitalLetter = true;
}
if (Character.isDigit(c)) {
hasNumber = true;
}
if (!Character.isLetterOrDigit(c)) {
hasSpecialCharacter = true;
}
}
return hasCapitalLetter && hasNumber && hasSpecialCharacter;
}
// Check South African  phone number
public boolean checkCellPhoneNumber() {
if (cellphonenumber == null) {
return false;
}

String regex = "^\\+27\\d{8}$";
return Pattern.matches(regex, cellphonenumber);
}
// Register the user
public String registerUser() {
if (!checkUserName()) {
return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
}
if (!checkPasswordComplexity()) {
return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
}
if (!checkCellPhoneNumber()) {
return "Cell phone number is incorrectly formatted or does not contain an international code; please correct the number and try again.";
}
return "Username successfully captured. "
+"Password successfully captured. "
+"Cell phone number successfully captured.";
}
// Login user
public boolean loginUser(String enteredUsername, String enteredPassword) {
return enteredUsername != null
&& enteredPassword != null
&& enteredUsername.equals(username)
&& enteredPassword.equals(password);
}
// Return login status message
public String returnLoginStatus(boolean loginStatus) {
if (loginStatus) {
return "Welcome <" + firstname + ">, <" + lastname
+"> it is great to see you again.";
} else {
return "Username or password incorrect, please try again.";
}
}
}