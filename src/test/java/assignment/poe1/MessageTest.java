
package assignment.poe1;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class MessageTest {

    private static final Path JSON_FILE = Path.of("messages.json");
    private String originalJson;

    @Before
    public void setUp() throws IOException {
        // Keep a copy of any messages.json file that existed before testing.
        originalJson = Files.exists(JSON_FILE)
                ? Files.readString(JSON_FILE)
                : null;

        Message.clearAllMessages();
        Files.deleteIfExists(JSON_FILE);
    }

    @After
    public void tearDown() throws IOException {
        Message.clearAllMessages();

        // Restore the original messages.json file.
        if (originalJson == null) {
            Files.deleteIfExists(JSON_FILE);
        } else {
            Files.writeString(JSON_FILE, originalJson);
        }
    }

    // ---------------------------------------------------------
    // PART 2 TESTS
    // ---------------------------------------------------------

    @Test
    public void generatedIdIsTenDigits() {
        Message message = new Message(0, "+27834557896", "Hello");

        assertTrue(message.getMessageID().matches("\\d{10}"));
    }

    @Test
    public void rejectsIdLongerThanTenCharacters() {
        Message message = new Message(
                "12345678901", 0, "+27834557896", "Hello");

        assertFalse(message.checkMessageID());
    }

    @Test
    public void acceptsAssignmentInternationalRecipient() {
        Message message = new Message(
                0, "+27834557896", "Hello");

        assertEquals(
                "Cell phone number successfully captured.",
                message.checkRecipientCell());
    }

    @Test
    public void rejectsBadRecipient() {
        Message message = new Message(
                0, "12345", "Hello");

        assertNotEquals(
                "Cell phone number successfully captured.",
                message.checkRecipientCell());
    }

    @Test
    public void accepts250Characters() {
        String text = "A".repeat(250);

        Message message = new Message(
                0, "+27834557896", text);

        assertEquals(
                "Message ready to send.",
                message.validateMessageLength());
    }

    @Test
    public void rejectsMoreThan250Characters() {
        String text = "A".repeat(251);

        Message message = new Message(
                0, "+27834557896", text);

        assertEquals(
                "Message exceeds 250 characters by 1; please reduce the size.",
                message.validateMessageLength());
    }

    @Test
    public void createsAssignmentHash() {
        Message message = new Message(
                "0012345678", 0, "+27834557896", "Hi tonight");

        assertEquals(
                "00:0:HITONIGHT",
                message.createMessageHash());
    }

    @Test
    public void sendsMessageAndCountsIt() {
        Message message = new Message(
                0, "+27834557896", "Hello");

        assertEquals(
                "Message successfully sent.",
                message.SentMessage("1"));

        assertTrue(message.isSent());
        assertEquals(1, message.returnTotalMessages());
    }

    @Test
    public void invalidMessageCannotBeSent() {
        Message message = new Message(
                "12345678901", 0, "+27834557896", "Hello");

        assertEquals(
                "Invalid message ID.",
                message.SentMessage("1"));

        assertEquals(0, message.returnTotalMessages());
    }

    @Test
    public void discardPromptAndDiscardWork() {
        Message message = new Message(
                0, "+27834557896", "Hello");

        assertEquals(
                "Press 0 to delete the message.",
                message.SentMessage("2"));

        assertEquals(
                "Message successfully disregarded.",
                message.disregardMessage());

        assertEquals(1, Message.getDisregardedMessages().length);
    }

    @Test
    public void storesMessageAsJson() throws IOException {
        Message message = new Message(
                "1234567890", 1, "+27834557896", "Hello");

        assertEquals(
                "Message successfully stored.",
                message.SentMessage("3"));

        assertTrue(Files.exists(JSON_FILE));

        String json = Files.readString(JSON_FILE);

        assertTrue(json.contains("\"messageHash\":\"12:1:HELLO\""));
        assertTrue(json.contains("\"recipient\":\"+27834557896\""));
        assertTrue(json.contains("\"message\":\"Hello\""));
    }

    @Test
    public void printsSentMessageDetailsInRequiredOrder() {
        Message message = new Message(
                0, "+27834557896", "Hello");

        message.SentMessage("1");

        String output = message.printMessages();

        assertTrue(output.contains("Message ID:"));
        assertTrue(output.contains("Message Hash:"));
        assertTrue(output.contains("Recipient:"));
        assertTrue(output.contains("Message:"));

        assertTrue(output.indexOf("Message ID:")
                < output.indexOf("Message Hash:"));

        assertTrue(output.indexOf("Message Hash:")
                < output.indexOf("Recipient:"));

        assertTrue(output.indexOf("Recipient:")
                < output.indexOf("Message:"));
    }

    // ---------------------------------------------------------
    // PART 3 TESTS
    // ---------------------------------------------------------

    @Test
    public void storesMessagesInRequiredArrays() {
        Message sent = new Message(
                "1000000001", 1, "+27834557896",
                "Did you get the cake?");

        Message stored = new Message(
                "1000000002", 2, "+27838884567",
                "Where are you? You are late! I have asked you to be on time.");

        Message disregarded = new Message(
                "1000000003", 3, "+27834484567",
                "Yoohoooo, I am at your gate.");

        sent.SentMessage("1");
        stored.SentMessage("3");
        disregarded.disregardMessage();

        assertEquals(1, Message.getSentMessages().length);
        assertEquals(1, Message.getStoredMessages().length);
        assertEquals(1, Message.getDisregardedMessages().length);

        assertEquals(3, Message.getMessageIDs().length);
        assertEquals(3, Message.getMessageHashes().length);
    }

    @Test
    public void displaysLongestStoredMessage() {
        Message first = new Message(
                "2000000001", 1, "+27838884567",
                "Short message");

        Message second = new Message(
                "2000000002", 2, "+27838884567",
                "Where are you? You are late! I have asked you to be on time.");

        first.SentMessage("3");
        second.SentMessage("3");

        Message longest = Message.getLongestStoredMessage();

        assertNotNull(longest);
        assertEquals(second.getMessageID(), longest.getMessageID());
    }

    @Test
    public void searchesStoredMessageByID() {
        Message message = new Message(
                "3000000001", 1, "+27838884567",
                "Where are you?");

        message.SentMessage("3");

        Message result =
                Message.searchStoredMessageByID("3000000001");

        assertNotNull(result);
        assertEquals(
                "Where are you?",
                result.getMessageText());
    }

    @Test
    public void searchesStoredMessagesByRecipient() {
        Message first = new Message(
                "4000000001", 1, "+27838884567",
                "Where are you?");

        Message second = new Message(
                "4000000002", 2, "+27838884567",
                "I am leaving now.");

        Message third = new Message(
                "4000000003", 3, "+27834557896",
                "Did you get the cake?");

        first.SentMessage("3");
        second.SentMessage("3");
        third.SentMessage("3");

        Message[] results =
                Message.searchStoredMessagesByRecipient("+27838884567");

        assertEquals(2, results.length);
        assertEquals(
                "+27838884567",
                results[0].getRecipient());
        assertEquals(
                "+27838884567",
                results[1].getRecipient());
    }

    @Test
    public void deletesStoredMessageUsingHash() throws IOException {
        Message message = new Message(
                "5000000001", 1, "+27838884567",
                "Ok, I am leaving without you.");

        message.SentMessage("3");

        String hash = message.createMessageHash();

        assertEquals(1, Message.getStoredMessages().length);

        boolean deleted =
                Message.deleteStoredMessageByHash(hash);

        assertTrue(deleted);
        assertEquals(0, Message.getStoredMessages().length);

        String json = Files.readString(JSON_FILE);
        assertFalse(json.contains(message.getMessageID()));
    }

    @Test
    public void generatesFullStoredMessagesReport() {
        Message first = new Message(
                "6000000001", 1, "+27838884567",
                "Where are you?");

        Message second = new Message(
                "6000000002", 2, "+27838884567",
                "Ok, I am leaving without you.");

        first.SentMessage("3");
        second.SentMessage("3");

        String report = Message.generateStoredMessagesReport();

        assertTrue(report.contains("6000000001"));
        assertTrue(report.contains("6000000002"));
        assertTrue(report.contains("Where are you?"));
        assertTrue(report.contains("Ok, I am leaving without you."));
        assertTrue(report.contains("Recipient:"));
        assertTrue(report.contains("Message Hash:"));
    }

    @Test
    public void loadsStoredMessagesFromJson() throws IOException {
        Message message = new Message(
                "7000000001", 1, "+27838884567",
                "Where are you?");

        message.SentMessage("3");

        Message.clearAllMessages();

        Message.loadStoredMessages();

        assertEquals(1, Message.getStoredMessages().length);
        assertEquals(
                "7000000001",
                Message.getStoredMessages()[0].getMessageID());
    }

    @Test
    public void returnsNoMessageWhenSearchingForUnknownID() {
        assertNull(
                Message.searchStoredMessageByID("9999999999"));
    }

    @Test
    public void returnsEmptyArrayWhenRecipientIsNotFound() {
        Message message = new Message(
                "8000000001", 1, "+27838884567",
                "Where are you?");

        message.SentMessage("3");

        Message[] results =
                Message.searchStoredMessagesByRecipient("+27834557896");

        assertEquals(0, results.length);
    }
}
