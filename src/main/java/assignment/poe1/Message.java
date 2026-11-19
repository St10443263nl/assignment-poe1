
package assignment.poe1;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

// This class handles creating, validating, sending, disregarding and storing messages.
public class Message {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final List<Message> SENT_MESSAGES = new ArrayList<>();
    private static final Path JSON_FILE = Path.of("messages.json");

    // Part 3: Arrays required to keep track of message information.
    private static Message[] sentMessages = new Message[0];
    private static Message[] disregardedMessages = new Message[0];
    private static Message[] storedMessages = new Message[0];
    private static String[] messageHashes = new String[0];
    private static String[] messageIDs = new String[0];

    private static final String RECIPIENT_SUCCESS =
            "Cell phone number successfully captured.";
    private static final String RECIPIENT_ERROR =
            "Cell phone number is incorrectly formatted or does not "
            + "contain an international code. Please correct the "
            + "number and try again.";
    private static final String LENGTH_SUCCESS = "Message ready to send.";

    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String messageText;
    private boolean sent;
    private boolean stored;

    public Message(int messageNumber, String recipient, String messageText) {
        this(generateID(), messageNumber, recipient, messageText);
    }

    public Message(String messageID, int messageNumber,
                   String recipient, String messageText) {
        this.messageID = messageID == null ? "" : messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient == null ? "" : recipient;
        this.messageText = messageText == null ? "" : messageText;
        this.sent = false;
        this.stored = false;
    }

    private static String generateID() {
        return String.format(Locale.ROOT, "%010d", RANDOM.nextInt(1_000_000_000));
    }

    public boolean checkMessageID() {
        return messageID.matches("\\d{1,10}");
    }

    public String checkRecipientCell() {
        boolean valid = Pattern.matches("\\+27\\d{9}", recipient)
                || Pattern.matches("0\\d{9,10}", recipient);
        return valid ? RECIPIENT_SUCCESS : RECIPIENT_ERROR;
    }

    public String validateMessageLength() {
        int length = messageText.length();
        if (length <= 250) {
            return LENGTH_SUCCESS;
        }
        return "Message exceeds 250 characters by " + (length - 250)
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
                ? messageID.substring(0, 2) : messageID;
        return (idPrefix + ":" + messageNumber + ":" + firstWord + lastWord)
                .toUpperCase(Locale.ROOT);
    }

    private String cleanWord(String word) {
        return word.replaceAll("[^\\p{L}\\p{N}]", "");
    }

    // Part 3: Generic array helpers.
    private static Message[] addToArray(Message[] array, Message message) {
        Message[] result = Arrays.copyOf(array, array.length + 1);
        result[result.length - 1] = message;
        return result;
    }

    private static String[] addToArray(String[] array, String value) {
        String[] result = Arrays.copyOf(array, array.length + 1);
        result[result.length - 1] = value;
        return result;
    }

    private static Message[] removeFromArray(Message[] array, String id) {
        List<Message> result = new ArrayList<>();
        for (Message message : array) {
            if (!message.getMessageID().equals(id)) {
                result.add(message);
            }
        }
        return result.toArray(new Message[0]);
    }

    private static boolean containsID(String id) {
        for (String existingID : messageIDs) {
            if (existingID.equals(id)) {
                return true;
            }
        }
        return false;
    }

    private static void registerMessageInformation(Message message) {
        if (!containsID(message.messageID)) {
            messageIDs = addToArray(messageIDs, message.messageID);
            messageHashes = addToArray(messageHashes, message.createMessageHash());
        }
    }

    private static void addSentMessage(Message message) {
        sentMessages = removeFromArray(sentMessages, message.messageID);
        sentMessages = addToArray(sentMessages, message);
    }

    private static void addDisregardedMessage(Message message) {
        disregardedMessages = removeFromArray(disregardedMessages, message.messageID);
        disregardedMessages = addToArray(disregardedMessages, message);
    }

    private static void addStoredMessage(Message message) {
        storedMessages = removeFromArray(storedMessages, message.messageID);
        storedMessages = addToArray(storedMessages, message);
    }

    public String SentMessage(String choice) {
        if (choice == null) {
            return "Invalid option selected. Enter 1, 2, or 3.";
        }

        switch (choice.trim()) {
            case "1":
                String sendValidation = validateBeforeProcessing();
                if (sendValidation != null) {
                    return sendValidation;
                }
                registerMessageInformation(this);
                storedMessages = removeFromArray(storedMessages, messageID);
                disregardedMessages = removeFromArray(disregardedMessages, messageID);
                addSentMessage(this);
                if (!SENT_MESSAGES.contains(this)) {
                    SENT_MESSAGES.add(this);
                }
                sent = true;
                stored = false;
                return "Message successfully sent.";

            case "2":
                return "Press 0 to delete the message.";

            case "3":
                String storeValidation = validateBeforeProcessing();
                if (storeValidation != null) {
                    return storeValidation;
                }
                try {
                    storeMessage();
                    registerMessageInformation(this);
                    SENT_MESSAGES.remove(this);
                    sentMessages = removeFromArray(sentMessages, messageID);
                    disregardedMessages = removeFromArray(disregardedMessages, messageID);
                    addStoredMessage(this);
                    sent = false;
                    stored = true;
                    return "Message successfully stored.";
                } catch (IOException ex) {
                    return "Message could not be stored: " + ex.getMessage();
                }

            default:
                return "Invalid option selected. Enter 1, 2, or 3.";
        }
    }

    private String validateBeforeProcessing() {
        if (!checkMessageID()) {
            return "Invalid message ID.";
        }
        String recipientResult = checkRecipientCell();
        if (!RECIPIENT_SUCCESS.equals(recipientResult)) {
            return recipientResult;
        }
        String lengthResult = validateMessageLength();
        if (!LENGTH_SUCCESS.equals(lengthResult)) {
            return lengthResult;
        }
        return null;
    }

    public String disregardMessage() {
        registerMessageInformation(this);
        SENT_MESSAGES.remove(this);
        sentMessages = removeFromArray(sentMessages, messageID);
        storedMessages = removeFromArray(storedMessages, messageID);
        addDisregardedMessage(this);
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

   // Saves the current message to the JSON file
// so that it can be retrieved later.
    public void storeMessage() throws IOException {
        List<Message> messages = readMessagesFromJson();
        messages.removeIf(message -> message.messageID.equals(messageID));
        messages.add(this);
        writeMessagesToJson(messages);
    }

   // Loads previously stored messages from the JSON file
// into the stored messages array when required.
    public static void loadStoredMessages() throws IOException {
        storedMessages = new Message[0];
        List<Message> messages = readMessagesFromJson();
        for (Message message : messages) {
            message.stored = true;
            message.sent = false;
            addStoredMessage(message);
            registerMessageInformation(message);
        }
    }

    private static List<Message> readMessagesFromJson() throws IOException {
        List<Message> messages = new ArrayList<>();
        if (!Files.exists(JSON_FILE)) {
            return messages;
        }
        String json = Files.readString(JSON_FILE, StandardCharsets.UTF_8).trim();
        if (json.isEmpty() || "[]".equals(json)) {
            return messages;
        }
        JsonArrayReader reader = new JsonArrayReader(json);
        for (Map<String, String> item : reader.readArray()) {
            String id = item.getOrDefault("messageID", "");
            String recipient = item.getOrDefault("recipient", "");
            String messageText = item.getOrDefault("message", "");
            int number = 0;
            try {
                number = Integer.parseInt(item.getOrDefault("messageNumber", "0"));
            } catch (NumberFormatException ignored) {
                // Older Part 2 JSON files did not save a message number.
            }
            Message message = new Message(id, number, recipient, messageText);
            message.stored = true;
            messages.add(message);
        }
        return messages;
    }

    private static void writeMessagesToJson(List<Message> messages) throws IOException {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < messages.size(); i++) {
            Message message = messages.get(i);
            if (i > 0) {
                json.append(',');
            }
            json.append("{\"messageID\":\"").append(jsonEscape(message.messageID))
                    .append("\",\"messageNumber\":").append(message.messageNumber)
                    .append(",\"messageHash\":\"").append(jsonEscape(message.createMessageHash()))
                    .append("\",\"recipient\":\"").append(jsonEscape(message.recipient))
                    .append("\",\"message\":\"").append(jsonEscape(message.messageText))
                    .append("\"}");
        }
        json.append(']');
        Files.writeString(JSON_FILE, json.toString(), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
    }

    private static String jsonEscape(String value) {
        StringBuilder escaped = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '"': escaped.append("\\\""); break;
                case '\\': escaped.append("\\\\"); break;
                case '\n': escaped.append("\\n"); break;
                case '\r': escaped.append("\\r"); break;
                case '\t': escaped.append("\\t"); break;
                case '\b': escaped.append("\\b"); break;
                case '\f': escaped.append("\\f"); break;
                default:
                    if (c < 0x20) {
                        escaped.append(String.format(Locale.ROOT, "\\u%04x", (int) c));
                    } else {
                        escaped.append(c);
                    }
            }
        }
        return escaped.toString();
    }

    private static String jsonUnescape(String value) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c != '\\' || i + 1 >= value.length()) {
                result.append(c);
                continue;
            }
            char next = value.charAt(++i);
            switch (next) {
                case '"': result.append('"'); break;
                case '\\': result.append('\\'); break;
                case '/': result.append('/'); break;
                case 'b': result.append('\b'); break;
                case 'f': result.append('\f'); break;
                case 'n': result.append('\n'); break;
                case 'r': result.append('\r'); break;
                case 't': result.append('\t'); break;
                case 'u':
                    if (i + 4 < value.length()) {
                        result.append((char) Integer.parseInt(value.substring(i + 1, i + 5), 16));
                        i += 4;
                    }
                    break;
                default: result.append(next); break;
            }
        }
        return result.toString();
    }

    // Small JSON reader for the flat string/number objects written by this class.
    private static final class JsonArrayReader {
        private final String input;
        private int position;

        JsonArrayReader(String input) {
            this.input = input;
        }

        List<Map<String, String>> readArray() throws IOException {
            List<Map<String, String>> result = new ArrayList<>();
            skipWhitespace();
            expect('[');
            skipWhitespace();
            if (peek(']')) {
                position++;
                return result;
            }
            while (position < input.length()) {
                result.add(readObject());
                skipWhitespace();
                if (peek(',')) {
                    position++;
                    skipWhitespace();
                } else if (peek(']')) {
                    position++;
                    break;
                } else {
                    throw new IOException("Invalid messages.json array.");
                }
            }
            return result;
        }

        private Map<String, String> readObject() throws IOException {
            Map<String, String> result = new HashMap<>();
            expect('{');
            skipWhitespace();
            if (peek('}')) {
                position++;
                return result;
            }
            while (position < input.length()) {
                String key = readString();
                skipWhitespace();
                expect(':');
                skipWhitespace();
                String value = peek('"') ? readString() : readNumberOrLiteral();
                result.put(key, value);
                skipWhitespace();
                if (peek(',')) {
                    position++;
                    skipWhitespace();
                } else if (peek('}')) {
                    position++;
                    break;
                } else {
                    throw new IOException("Invalid messages.json object.");
                }
            }
            return result;
        }

        private String readString() throws IOException {
            expect('"');
            StringBuilder raw = new StringBuilder();
            boolean escaped = false;
            while (position < input.length()) {
                char c = input.charAt(position++);
                if (!escaped && c == '"') {
                    return jsonUnescape(raw.toString());
                }
                raw.append(c);
                if (!escaped && c == '\\') {
                    escaped = true;
                } else {
                    escaped = false;
                }
            }
            throw new IOException("Unterminated string in messages.json.");
        }

        private String readNumberOrLiteral() {
            int start = position;
            while (position < input.length() && ",}] \t\r\n".indexOf(input.charAt(position)) == -1) {
                position++;
            }
            return input.substring(start, position);
        }

        private void expect(char expected) throws IOException {
            skipWhitespace();
            if (position >= input.length() || input.charAt(position) != expected) {
                throw new IOException("Invalid messages.json format.");
            }
            position++;
        }

        private boolean peek(char value) {
            return position < input.length() && input.charAt(position) == value;
        }

        private void skipWhitespace() {
            while (position < input.length() && Character.isWhitespace(input.charAt(position))) {
                position++;
            }
        }
    }

    // Part 3: Stored-message functions.
    public static Message[] getSentMessages() {
        return Arrays.copyOf(sentMessages, sentMessages.length);
    }

    public static Message[] getDisregardedMessages() {
        return Arrays.copyOf(disregardedMessages, disregardedMessages.length);
    }

    public static Message[] getStoredMessages() {
        return Arrays.copyOf(storedMessages, storedMessages.length);
    }

    public static String[] getMessageHashes() {
        return Arrays.copyOf(messageHashes, messageHashes.length);
    }

    public static String[] getMessageIDs() {
        return Arrays.copyOf(messageIDs, messageIDs.length);
    }

    public static Message getLongestStoredMessage() {
        Message longest = null;
        for (Message message : storedMessages) {
            if (longest == null || message.messageText.length() > longest.messageText.length()) {
                longest = message;
            }
        }
        return longest;
    }

    public static Message searchStoredMessageByID(String id) {
        for (Message message : storedMessages) {
            if (message.messageID.equals(id)) {
                return message;
            }
        }
        return null;
    }

    public static Message[] searchStoredMessagesByRecipient(String recipient) {
        List<Message> matches = new ArrayList<>();
        for (Message message : storedMessages) {
            if (message.recipient.equalsIgnoreCase(recipient == null ? "" : recipient.trim())) {
                matches.add(message);
            }
        }
        return matches.toArray(new Message[0]);
    }

    public static boolean deleteStoredMessageByHash(String hash) throws IOException {
        List<Message> messages = readMessagesFromJson();
        boolean removed = messages.removeIf(message -> message.createMessageHash().equalsIgnoreCase(hash));
        if (removed) {
            writeMessagesToJson(messages);
            storedMessages = removeStoredByHash(storedMessages, hash);
        }
        return removed;
    }

    private static Message[] removeStoredByHash(Message[] source, String hash) {
        List<Message> remaining = new ArrayList<>();
        for (Message message : source) {
            if (!message.createMessageHash().equalsIgnoreCase(hash)) {
                remaining.add(message);
            }
        }
        return remaining.toArray(new Message[0]);
    }

    public static String generateStoredMessagesReport() {
        if (storedMessages.length == 0) {
            return "There are no stored messages.";
        }
        StringBuilder report = new StringBuilder();
        for (Message message : storedMessages) {
            if (report.length() > 0) {
                report.append(System.lineSeparator()).append(System.lineSeparator());
            }
            report.append(message.toDisplayString());
        }
        return report.toString();
    }

    public static void clearAllMessages() {
        SENT_MESSAGES.clear();
        sentMessages = new Message[0];
        disregardedMessages = new Message[0];
        storedMessages = new Message[0];
        messageHashes = new String[0];
        messageIDs = new String[0];
    }

    public String toDisplayString() {
        return "Message ID: " + messageID + System.lineSeparator()
                + "Message Hash: " + createMessageHash() + System.lineSeparator()
                + "Recipient: " + recipient + System.lineSeparator()
                + "Message: " + messageText;
    }

    public String getMessageID() { return messageID; }
    public String getRecipient() { return recipient; }
    public String getMessageText() { return messageText; }
    public int getMessageNumber() { return messageNumber; }
    public boolean isSent() { return sent; }
    public boolean isStored() { return stored; }

    public static void clearSentMessages() {
        SENT_MESSAGES.clear();
        sentMessages = new Message[0];
    }
}
