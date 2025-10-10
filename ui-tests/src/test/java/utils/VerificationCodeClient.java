package utils;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.search.*;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerificationCodeClient {

    public static String fetchCodeForEmail(String recipientEmail) {
        String sys = System.getProperty("reg.code");
        if (sys != null && sys.matches("\\d{6}")) return sys;

        String env = System.getenv("REG_CODE");
        if (env != null && env.matches("\\d{6}")) return env;

        String strategy = Optional.ofNullable(ConfigurationReader.get("reg.code.strategy")).orElse("env").trim();
        if ("imap".equalsIgnoreCase(strategy)) {
            return fetchFromImap(recipientEmail);
        }
        return null;
    }

    private static String fetchFromImap(String recipientEmail) {
        String protocol = Optional.ofNullable(ConfigurationReader.get("mail.store.protocol")).orElse("imaps");
        String host = Optional.ofNullable(ConfigurationReader.get("mail.imaps.host"))
                .orElse(Optional.ofNullable(ConfigurationReader.get("mail.imap.host")).orElse("imap.gmail.com"));
        String port = Optional.ofNullable(ConfigurationReader.get("mail.imaps.port"))
                .orElse(Optional.ofNullable(ConfigurationReader.get("mail.imap.port")).orElse("993"));
        String username = ConfigurationReader.get("mail.username");
        String appPassword = ConfigurationReader.get("mail.app.password");
        String folderName = Optional.ofNullable(ConfigurationReader.get("mail.folder")).orElse("INBOX");

        boolean ssl = Boolean.parseBoolean(Optional.ofNullable(ConfigurationReader.get("mail.imap.ssl.enable")).orElse("true"));
        boolean debug = Boolean.parseBoolean(Optional.ofNullable(ConfigurationReader.get("mail.debug")).orElse("false"));
        boolean deleteAfterRead = Boolean.parseBoolean(Optional.ofNullable(ConfigurationReader.get("mail.delete.after.read")).orElse("false"));

        int timeoutSec = intProp("mail.wait.timeoutSec", 120);
        int pollSec = intProp("mail.poll.intervalSec", 5);

        String subjectContains = Optional.ofNullable(ConfigurationReader.get("mail.search.subject.contains")).orElse("код,code,verification");
        String fromContains = Optional.ofNullable(ConfigurationReader.get("mail.search.from")).orElse("");
        String regex = Optional.ofNullable(ConfigurationReader.get("mail.code.regex")).orElse("\\b(\\d{6})\\b");

        Properties props = new Properties();
        props.put("mail.store.protocol", protocol);
        if ("imaps".equalsIgnoreCase(protocol)) {
            props.put("mail.imaps.host", host);
            props.put("mail.imaps.port", port);
            props.put("mail.imaps.ssl.enable", String.valueOf(ssl));
        } else {
            props.put("mail.imap.host", host);
            props.put("mail.imap.port", port);
            props.put("mail.imap.ssl.enable", String.valueOf(ssl));
        }

        Session session = Session.getInstance(props);
        session.setDebug(debug);

        Store store = null;
        Folder folder = null;
        try {
            store = session.getStore(protocol);
            store.connect(host, username, appPassword);

            folder = store.getFolder(folderName);
            folder.open(Folder.READ_WRITE);

            long start = System.currentTimeMillis();
            Pattern codePattern = Pattern.compile(regex);

            while (System.currentTimeMillis() - start < timeoutSec * 1000L) {
                SearchTerm term = composeSearchTerm(recipientEmail, subjectContains, fromContains);
                Message[] msgs = folder.search(term);

                Arrays.sort(msgs, Comparator.comparing(VerificationCodeClient::received).reversed());

                for (Message m : msgs) {
                    String text = extractText(m);
                    if (text == null) continue;
                    Matcher matcher = codePattern.matcher(text);
                    if (matcher.find()) {
                        String code = matcher.group(1);
                        if (deleteAfterRead) {
                            m.setFlag(Flags.Flag.DELETED, true);
                        } else {
                            m.setFlag(Flags.Flag.SEEN, true);
                        }
                        folder.close(deleteAfterRead);
                        store.close();
                        return code;
                    }
                }

                Thread.sleep(pollSec * 1000L);
                folder.getMessageCount(); // ping
            }
        } catch (Exception ignored) {
        } finally {
            try { if (folder != null && folder.isOpen()) folder.close(deleteAfterRead); } catch (Exception ignored) {}
            try { if (store != null && store.isConnected()) store.close(); } catch (Exception ignored) {}
        }
        return null;
    }

    private static SearchTerm composeSearchTerm(String recipientEmail, String subjectContainsCsv, String fromContains) {
        List<SearchTerm> terms = new ArrayList<>();

        terms.add(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

        Date since = Date.from(Instant.now().minusSeconds(60 * 30)); // последние 30 минут
        terms.add(new ReceivedDateTerm(ComparisonTerm.GE, since));

        if (recipientEmail != null && !recipientEmail.isBlank()) {
            terms.add(new RecipientStringTerm(Message.RecipientType.TO, recipientEmail));
        }

        if (fromContains != null && !fromContains.isBlank()) {
            terms.add(new FromStringTerm(fromContains));
        }

        if (subjectContainsCsv != null && !subjectContainsCsv.isBlank()) {
            String[] keys = Arrays.stream(subjectContainsCsv.split(","))
                    .map(String::trim).filter(s -> !s.isEmpty()).toArray(String[]::new);
            if (keys.length == 1) {
                terms.add(new SubjectTerm(keys[0]));
            } else if (keys.length > 1) {
                SearchTerm or = new SubjectTerm(keys[0]);
                for (int i = 1; i < keys.length; i++) {
                    or = new OrTerm(or, new SubjectTerm(keys[i]));
                }
                terms.add(or);
            }
        }

        SearchTerm and = terms.get(0);
        for (int i = 1; i < terms.size(); i++) {
            and = new AndTerm(and, terms.get(i));
        }
        return and;
    }

    private static Date received(Message m) {
        try {
            Date d = m.getReceivedDate();
            if (d != null) return d;
            d = m.getSentDate();
            return d != null ? d : Date.from(Instant.now());
        } catch (Exception e) {
            return Date.from(Instant.now());
        }
    }

    private static String extractText(Message message) {
        try {
            Object content = message.getContent();
            if (content instanceof String s) {
                return s;
            }
            if (content instanceof Multipart mp) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < mp.getCount(); i++) {
                    BodyPart bp = mp.getBodyPart(i);
                    Object part = bp.getContent();
                    if (part instanceof String ps) {
                        sb.append(ps).append('\n');
                    }
                }
                return sb.toString();
            }
        } catch (Exception ignored) {}
        return null;
    }

    private static int intProp(String key, int def) {
        try { return Integer.parseInt(Optional.ofNullable(ConfigurationReader.get(key)).orElse(String.valueOf(def))); }
        catch (Exception e) { return def; }
    }
}