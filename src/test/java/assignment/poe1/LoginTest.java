package assignment.poe1;
//Unit test for usernam,password and cellphone number and login validation
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class LoginTest {

    @Test
    public void testUsernameCorrectlyFormatted() {

        Login user = new Login(
                "kyl_1",
                "Ch&sec@ke99!",
                "+2783986976",
                "Kyle",
                "Smith"
        );

        assertEquals(true, user.checkUserName());
    }

    @Test
    public void testUsernameIncorrectlyFormatted() {

        Login user = new Login(
                "kyle123",
                "Ch&sec@ke99!",
                "+2783986976",
                "Kyle",
                "Smith"
        );

        assertEquals(false, user.checkUserName());
    }

    @Test
    public void testPasswordMeetsComplexity() {

        Login user = new Login(
                "kyl_1",
                "Ch&sec@ke99!",
                "+2783986976",
                "Kyle",
                "Smith"
        );

        assertEquals(true, user.checkPasswordComplexity());
    }

    @Test
    public void testPasswordDoesNotMeetComplexity() {

        Login user = new Login(
                "kyl_1",
                "password",
                "+2783986976",
                "Kyle",
                "Smith"
        );

        assertEquals(false, user.checkPasswordComplexity());
    }

    @Test
    public void testCellPhoneCorrectlyFormatted() {

        Login user = new Login(
                "kyl_1",
                "Ch&sec@ke99!",
                "+2783986976",
                "Kyle",
                "Smith"
        );

        assertEquals(true, user.checkCellPhoneNumber());
    }

    @Test
    public void testCellPhoneIncorrectlyFormatted() {

        Login user = new Login(
                "kyl_1",
                "Ch&sec@ke99!",
                "08966553",
                "Kyle",
                "Smith"
        );

        assertEquals(false, user.checkCellPhoneNumber());
    }

    @Test
    public void testLoginSuccessful() {

        Login user = new Login(
                "kyl_1",
                "Ch&sec@ke99!",
                "+2783986976",
                "Kyle",
                "Smith"
        );

        assertEquals(
                true,
                user.loginUser("kyl_1", "Ch&sec@ke99!")
        );
    }

    @Test
    public void testLoginFailed() {

        Login user = new Login(
                "kyl_1",
                "Ch&sec@ke99!",
                "+2783986976",
                "Kyle",
                "Smith"
        );

        assertEquals(
                false,
                user.loginUser("kyl_1", "wrongpassword")
        );
    }

    @Test
    public void testSuccessfulLoginMessage() {

        Login user = new Login(
                "kyl_1",
                "Ch&sec@ke99!",
                "+2783986976",
                "Kyle",
                "Smith"
        );

        assertEquals(
                "Welcome <Kyle>, <Smith> it is great to see you again.",
                user.returnLoginStatus(true)
        );
    }

    @Test
    public void testFailedLoginMessage() {

        Login user = new Login(
                "kyl_1",
                "Ch&sec@ke99!",
                "+2783986976",
                "Kyle",
                "Smith"
        );

        assertEquals(
                "Username or password incorrect, please try again.",
                user.returnLoginStatus(false)
        );
    }
}