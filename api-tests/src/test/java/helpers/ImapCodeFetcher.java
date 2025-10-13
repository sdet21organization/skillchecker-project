package helpers;

import javax.mail.*;
import javax.mail.search.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImapCodeFetcher {

    private final String imapHost;
    private final int imapPort;
    private final String username;
    private final String password;
    private final String fromEmail;
    private final int timeoutSec;
    private final int pollIntervalSec;
    private final boolean debug;
    private final Pattern codePattern;

    public ImapCodeFetcher(Properties config) {
        this.imapHost = config.getProperty("mail.imap.host");
        this.imapPort = Integer.parseInt(config.getProperty("mail.imap.port"));
        this.username = config.getProperty("mail.username");
        this.password = config.getProperty("mail.app.password");
        this.fromEmail = config.getProperty("mail.search.from", "skillchecker");
        this.timeoutSec = Integer.parseInt(config.getProperty("mail.wait.timeoutSec", "30"));
        this.pollIntervalSec = Integer.parseInt(config.getProperty("mail.poll.intervalSec", "3"));
        this.debug = Boolean.parseBoolean(config.getProperty("mail.debug", "false"));
        String regex = config.getProperty("mail.code.regex", "\\b(\\d{6})\\b");
        this.codePattern = Pattern.compile(regex);
    }

    public static String fetchVerificationCode(String targetEmail, long searchStartTimeMillis) {
        Properties config = ConfigurationReader.getAllProperties();
        ImapCodeFetcher fetcher = new ImapCodeFetcher(config);
        try {
            return fetcher.fetchCode(targetEmail, searchStartTimeMillis);
        } catch (Exception e) {
            System.err.println("IMAP fetch error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private String fetchCode(String targetEmail, long searchStartTimeMillis) throws Exception {
        Store store = null;
        Folder folder = null;

        try {
            Date searchStartTime = new Date(searchStartTimeMillis);

            Properties props = new Properties();
            props.put("mail.store.protocol", "imaps");
            props.put("mail.imaps.host", imapHost);
            props.put("mail.imaps.port", imapPort);
            props.put("mail.imaps.ssl.enable", "true");
            props.put("mail.debug", debug);

            Session session = Session.getInstance(props);
            store = session.getStore("imaps");
            store.connect(imapHost, username, password);

            folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            SearchTerm searchTerm = new AndTerm(
                    new SubjectTerm("code"),
                    new FromStringTerm(fromEmail)
            );

            long timeout = System.currentTimeMillis() + (timeoutSec * 1000L);

            while (System.currentTimeMillis() < timeout) {
                Message[] messages = folder.search(searchTerm);

                if (messages.length == 0) {
                    folder.close(false);
                    try {
                        folder = store.getFolder("[Gmail]/Promotions");
                        folder.open(Folder.READ_ONLY);
                        messages = folder.search(searchTerm);
                    } catch (Exception e) {
                        folder = store.getFolder("INBOX");
                        folder.open(Folder.READ_ONLY);
                    }
                }

                if (messages.length > 0) {
                    Arrays.sort(messages, (m1, m2) -> {
                        try {
                            return m2.getReceivedDate().compareTo(m1.getReceivedDate());
                        } catch (Exception e) {
                            return 0;
                        }
                    });

                    for (Message message : messages) {
                        Date receivedDate = message.getReceivedDate();

                        if (receivedDate.before(searchStartTime)) {
                            continue;
                        }

                        Address[] recipients = message.getAllRecipients();
                        boolean isForTargetEmail = false;

                        if (recipients != null) {
                            for (Address addr : recipients) {
                                String recipientEmail = addr.toString().toLowerCase();
                                String normalizedRecipient = recipientEmail.replace(".", "");
                                String normalizedTarget = targetEmail.toLowerCase().replace(".", "");

                                if (recipientEmail.contains(targetEmail.toLowerCase()) ||
                                        normalizedRecipient.contains(normalizedTarget)) {
                                    isForTargetEmail = true;
                                    break;
                                }
                            }
                        }

                        if (isForTargetEmail) {
                            String text = this.readAsText(message);
                            String code = this.extractCode(text);
                            if (code != null) {
                                return code;
                            }
                        }
                    }
                }

                Thread.sleep(pollIntervalSec * 1000);
            }

            return null;

        } finally {
            if (folder != null && folder.isOpen()) {
                folder.close(false);
            }
            if (store != null && store.isConnected()) {
                store.close();
            }
        }
    }

    private String readAsText(Message message) throws Exception {
        Object content = message.getContent();
        if (content instanceof String) {
            return (String) content;
        } else if (content instanceof Multipart) {
            return getTextFromMultipart((Multipart) content);
        }
        return "";
    }

    private String getTextFromMultipart(Multipart multipart) throws Exception {
        StringBuilder result = new StringBuilder();
        int count = multipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append(bodyPart.getContent());
            } else if (bodyPart.isMimeType("text/html")) {
                result.append(bodyPart.getContent());
            } else if (bodyPart.getContent() instanceof Multipart) {
                result.append(getTextFromMultipart((Multipart) bodyPart.getContent()));
            }
        }
        return result.toString();
    }

    private String extractCode(String text) {
        if (text == null) return null;
        Matcher matcher = codePattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}