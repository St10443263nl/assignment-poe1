
package assignment.poe1;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class MessageTest {

    private final Path jsonFile = Path.of("messages.json");
    private byte[] previousJson;

    @Before
    public void setUp() throws IOException {
        Message.clearSentMessages();

        previousJson = Files.exists(jsonFile)
                ? Files.readAllBytes(jsonFile)
                : null;

        Files.deleteIfExists(jsonFile);
    }

    @After
    public void tearDown() throws IOException {
        Message.clearSentMessages();

        if (previousJson == null) {
            Files.deleteIfExists(jsonFile);
        } else {
            Files.write(jsonFile, previousJson);
        }
    }

    @Test
    public void generatedIdIsTenDigits() {
        Message m = new Message(
                0,
                "+27718693002",
                "Hi Mike, can you join us for dinner tonight?"
        );

        assertTrue(m.checkMessageID());
        assertEquals(10, m.getMessageID().length());
        assertTrue(m.getMessageID().matches("\\d{10}"));
    }

    @Test
    public void rejectsIdLongerThanTenCharacters() {
        Message m = new Message(
                "12345678901", 0, "+27718693002", "Hi"
        );

        assertFalse(m.checkMessageID());
    }

    @Test
    public void acceptsAssignmentInternationalRecipient() {
        Message m = new Message(
                0, "+27718693002", "Hi"
        );

        assertEquals(
                "Cell phone number successfully captured.",
                m.checkRecipientCell()
        );
    }

    @Test
    public void acceptsSecondAssignmentTestNumber() {
        Message m = new Message(
                1, "08575975889", "Hi"
        );

        assertEquals(
                "Cell phone number successfully captured.",
                m.checkRecipientCell()
        );
    }

    @Test
    public void rejectsBadRecipient() {
        Message m = new Message(
                0, "12345", "Hi"
        );

        assertEquals(
                "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.",
                m.checkRecipientCell()
        );
    }

    @Test
    public void accepts250Characters() {
        Message m = new Message(
                0, "+27718693002", "A".repeat(250)
        );

        assertEquals(
                "Message ready to send.",
                m.validateMessageLength()
        );
    }

    @Test
    public void rejectsMoreThan250Characters() {
        Message m = new Message(
                0, "+27718693002", "A".repeat(255)
        );

        assertEquals(
                "Message exceeds 250 characters by 5; please reduce the size.",
                m.validateMessageLength()
        );
    }

    @Test
    public void createsAssignmentHash() {
        Message m = new Message(
                "0012345678",
                0,
                "+27718693002",
                "Hi Mike, can you join us for dinner tonight?"
        );

        assertEquals(
                "00:0:HITONIGHT",
                m.createMessageHash()
        );
    }

    @Test
    public void sendsMessageAndCountsIt() {
        Message m = new Message(
                "1234567890", 0, "+27718693002", "Hello"
        );

        assertEquals(
                "Message successfully sent.",
                m.SentMessage("1")
        );

        assertTrue(m.isSent());
        assertEquals(1, m.returnTotalMessagess());
    }

    @Test
    public void invalidMessageCannotBeSent() {
        Message m = new Message(
                "1234567890", 0, "12345", "Hello"
        );

        assertTrue(
                m.SentMessage("1").startsWith(
                        "Cell phone number is incorrectly formatted"
                )
        );

        assertEquals(0, m.returnTotalMessagess());
    }

    @Test
    public void discardPromptAndDiscardWork() {
        Message m = new Message(
                "1234567890", 0, "+27718693002", "Hello"
        );

        m.SentMessage("1");

        assertEquals(
                "Press 0 to delete the message.",
                m.SentMessage("2")
        );

        assertEquals(
                "Message successfully disregarded.",
                m.disregardMessage()
        );

        assertEquals(0, m.returnTotalMessagess());
    }

    @Test
    public void storesMessageAsJson() throws IOException {
        Message m = new Message(
                "1234567890", 1, "+27718693002", "Hello"
        );

        assertEquals(
                "Message successfully stored.",
                m.SentMessage("3")
        );

        assertTrue(
                "messages.json was not created.",
                Files.exists(jsonFile)
        );

        String json = Files.readString(jsonFile).trim();

        System.out.println("JSON CONTENT: " + json);

        assertTrue(
                "JSON must start with [ and end with ]. Actual: " + json,
                json.startsWith("[") && json.endsWith("]")
        );

        assertTrue(
                "Missing message ID. Actual: " + json,
                json.contains("\"messageID\":\"1234567890\"")
        );

        assertTrue(
                "Missing or incorrect message hash. Actual: " + json,
                json.contains("\"messageHash\":\"12:1:HELLO\"")
        );
    }

    @Test
    public void printsSentMessageDetailsInRequiredOrder() {
        Message m = new Message(
                "1234567890", 1, "+27718693002", "Hello"
        );

        m.SentMessage("1");

        String output = m.printMessages();

        assertTrue(
                output.indexOf("Message ID:")
                        < output.indexOf("Message Hash:")
        );

        assertTrue(
                output.indexOf("Message Hash:")
                        < output.indexOf("Recipient:")
        );

        assertTrue(
                output.indexOf("Recipient:")
                        < output.indexOf("Message:")
        );
    }
}
