
package assignment.poe1;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

// This class handles creating, validating, and storing messages.
public class Message {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final List<Message> SENT_MESSAGES = new ArrayList<>();
    private static final Path JSON_FILE = Path.of("messages.json");

    private static final String RECIPIENT_SUCCESS =
            "Cell phone number successfully captured.";

    private static final String RECIPIENT_ERROR =
            "Cell phone number is incorrectly formatted or does not "
            + "contain an international code. Please correct the "
            + "number and try again.";

    private static final String LENGTH_SUCCESS =
            "Message ready to send.";

    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String messageText;

    private boolean sent;
    private boolean stored;

    public Message(int messageNumber, String recipient, String messageText) {
        this(generateID(), messageNumber, recipient, messageText);
    }

    public Message(
            String messageID,
            int messageNumber,
            String recipient,
            String messageText) {

        this.messageID = messageID == null ? "" : messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient == null ? "" : recipient;
        this.messageText = messageText == null ? "" : messageText;

        this.sent = false;
        this.stored = false;
    }

    private static String generateID() {
        return String.format(
                Locale.ROOT,
                "%010d",
                RANDOM.nextInt(1_000_000_000)
        );
    }

    public boolean checkMessageID() {
        return messageID.matches("\\d{1,10}");
    }

    public String checkRecipientCell() {

        boolean valid =
                Pattern.matches("\\+27\\d{9}", recipient)
                || Pattern.matches("0\\d{9,10}", recipient);

        return valid ? RECIPIENT_SUCCESS : RECIPIENT_ERROR;
    }

    public String validateMessageLength() {

        int length = messageText.length();

        if (length <= 250) {
            return LENGTH_SUCCESS;
        }

        return "Message exceeds 250 characters by "
                + (length - 250)
                + "; please reduce the size.";
    }

    public String createMessageHash() {

        String trimmedMessage = messageText.trim();

        String firstWord = "";
        String lastWord = "";

        if (!trimmedMessage.isEmpty()) {

            String[] words = trimmedMessage.split("\\s+");

            firstWord = cleanWord(words[0]);

            if (words.length > 1) {
                lastWord = cleanWord(words[words.length - 1]);
            }
        }

        String idPrefix = messageID.length() >= 2
                ? messageID.substring(0, 2)
                : messageID;

        return (idPrefix + ":"
                + messageNumber + ":"
                + firstWord + lastWord).toUpperCase(Locale.ROOT);
    }

    private String cleanWord(String word) {
        return word.replaceAll("[^\\p{L}\\p{N}]", "");
    }

    public String SentMessage(String choice) {

        if (choice == null) {
            return "Invalid option selected. Enter 1, 2, or 3.";
        }

        switch (choice.trim()) {

            case "1":

                if (!checkMessageID()) {
                    return "Invalid message ID.";
                }

                String recipientResult = checkRecipientCell();

                if (!recipientResult.equals(RECIPIENT_SUCCESS)) {
                    return recipientResult;
                }

                String lengthResult = validateMessageLength();

                if (!lengthResult.equals(LENGTH_SUCCESS)) {
                    return lengthResult;
                }

                if (!sent) {
                    SENT_MESSAGES.add(this);
                }

                sent = true;
                stored = false;

                return "Message successfully sent.";

            case "2":

                return "Press 0 to delete the message.";

            case "3":

                if (!checkMessageID()) {
                    return "Invalid message ID.";
                }

                String storeRecipientResult = checkRecipientCell();

                if (!storeRecipientResult.equals(RECIPIENT_SUCCESS)) {
                    return storeRecipientResult;
                }

                String storeLengthResult = validateMessageLength();

                if (!storeLengthResult.equals(LENGTH_SUCCESS)) {
                    return storeLengthResult;
                }

                try {
                    storeMessage();

                    SENT_MESSAGES.remove(this);
                    sent = false;
                    stored = true;

                    return "Message successfully stored.";

                } catch (IOException e) {
                    return "Message could not be stored: "
                            + e.getMessage();
                }

            default:

                return "Invalid option selected. Enter 1, 2, or 3.";
        }
    }

    public String disregardMessage() {

        SENT_MESSAGES.remove(this);
        sent = false;
        stored = false;

        return "Message successfully disregarded.";
    }

    public String printMessages() {

        StringBuilder output = new StringBuilder();

        for (Message message : SENT_MESSAGES) {

            if (output.length() > 0) {
                output.append(System.lineSeparator());
            }

            output.append(message.toDisplayString());
        }

        return output.toString();
    }

    public int returnTotalMessagess() {
        return SENT_MESSAGES.size();
    }

    public int returnTotalMessages() {
        return returnTotalMessagess();
    }

    public void storeMessage() throws IOException {

        String item = "{\"messageID\":\""
                + jsonEscape(messageID)
                + "\",\"messageHash\":\""
                + jsonEscape(createMessageHash())
                + "\",\"recipient\":\""
                + jsonEscape(recipient)
                + "\",\"message\":\""
                + jsonEscape(messageText)
                + "\"}";

        String existingContent = "";

        if (Files.exists(JSON_FILE)) {
            existingContent = Files.readString(
                    JSON_FILE,
                    StandardCharsets.UTF_8
            ).trim();
        }

        String json;

        if (existingContent.isEmpty()
                || existingContent.equals("[]")) {

            json = "[" + item + "]";

        } else if (existingContent.startsWith("[")
                && existingContent.endsWith("]")) {

            String content = existingContent.substring(
                    1,
                    existingContent.length() - 1
            ).trim();

            if (content.isEmpty()) {
                json = "[" + item + "]";
            } else {
                json = "[" + content + "," + item + "]";
            }

        } else {
            throw new IOException(
                    "Existing messages.json is not a valid JSON array."
            );
        }

        Files.writeString(
                JSON_FILE,
                json,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        );
    }

    private static String jsonEscape(String value) {

        StringBuilder escaped = new StringBuilder();

        for (int i = 0; i < value.length(); i++) {

            char c = value.charAt(i);

            switch (c) {

                case '"':
                    escaped.append("\\\"");
                    break;

                case '\\':
                    escaped.append("\\\\");
                    break;

                case '\n':
                    escaped.append("\\n");
                    break;

                case '\r':
                    escaped.append("\\r");
                    break;

                case '\t':
                    escaped.append("\\t");
                    break;

                case '\b':
                    escaped.append("\\b");
                    break;

                case '\f':
                    escaped.append("\\f");
                    break;

                default:
                    if (c < 0x20) {
                        escaped.append(String.format(
                                Locale.ROOT,
                                "\\u%04x",
                                (int) c
                        ));
                    } else {
                        escaped.append(c);
                    }
                    break;
            }
        }

        return escaped.toString();
    }

    public String toDisplayString() {

        return "Message ID: " + messageID
                + System.lineSeparator()
                + "Message Hash: " + createMessageHash()
                + System.lineSeparator()
                + "Recipient: " + recipient
                + System.lineSeparator()
                + "Message: " + messageText;
    }

    public String getMessageID() {
        return messageID;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessageText() {
        return messageText;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public boolean isSent() {
        return sent;
    }

    public boolean isStored() {
        return stored;
    }

    public static void clearSentMessages() {
        SENT_MESSAGES.clear();
    }
}
