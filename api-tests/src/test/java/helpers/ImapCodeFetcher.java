package helpers;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.search.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImapCodeFetcher {

    private final String host;
    private final int port;
    private final String user;
    private final String pass;
    private final String fromFilter;
    private final String subjectFilter;
    private final int timeoutSec;
    private final int pollSec;
    private final boolean debug;
    private final boolean deleteAfterRead;
    private final Pattern codeRx;

    public ImapCodeFetcher(Properties c) {
        this.host = c.getProperty("mail.imap.host");
        this.port = Integer.parseInt(c.getProperty("mail.imap.port"));
        this.user = c.getProperty("mail.username");
        this.pass = c.getProperty("mail.app.password");
        this.fromFilter = c.getProperty("mail.search.from", "no-reply@skillchecker.tech");
        this.subjectFilter = c.getProperty("mail.search.subject.contains", "verification code");
        this.timeoutSec = Integer.parseInt(c.getProperty("mail.wait.timeoutSec", "180"));
        this.pollSec = Integer.parseInt(c.getProperty("mail.poll.intervalSec", "3"));
        this.debug = Boolean.parseBoolean(c.getProperty("mail.debug", "false"));
        this.deleteAfterRead = Boolean.parseBoolean(c.getProperty("mail.delete.after.read", "false"));
        this.codeRx = Pattern.compile(c.getProperty("mail.code.regex", "\\b(\\d{6})\\b"));
    }

    public static String fetchVerificationCode(String targetEmail, long sendMillis) {
        Properties cfg = ConfigurationReader.getAllProperties();
        try {
            return new ImapCodeFetcher(cfg).fetch(targetEmail, sendMillis);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String fetch(String targetEmail, long sendMillis) throws Exception {
        Date since = new Date(sendMillis);

        Properties p = new Properties();
        p.put("mail.store.protocol", "imaps");
        p.put("mail.imaps.host", host);
        p.put("mail.imaps.port", String.valueOf(port));
        p.put("mail.imaps.ssl.enable", "true");
        p.put("mail.imaps.starttls.enable", "true");
        p.put("mail.imaps.connectiontimeout", "30000");
        p.put("mail.imaps.timeout", "30000");
        p.put("mail.debug", String.valueOf(debug));

        Session s = Session.getInstance(p);
        Store store = s.getStore("imaps");
        store.connect(host, user, pass);

        List<String> folders = Arrays.asList("INBOX", "[Gmail]/All Mail");
        long deadline = System.currentTimeMillis() + timeoutSec * 1000L;

        try {
            while (System.currentTimeMillis() < deadline) {
                for (String fn : folders) {
                    Folder f = store.getFolder(fn);
                    if (f == null || !f.exists()) continue;
                    boolean needWrite = deleteAfterRead;
                    f.open(needWrite ? Folder.READ_WRITE : Folder.READ_ONLY);

                    try {
                        SearchTerm dateTerm = new ReceivedDateTerm(ComparisonTerm.GE, since);
                        SearchTerm fromTerm = new FromStringTerm(fromFilter);
                        SearchTerm subjTerm = new OrTerm(new SubjectTerm(subjectFilter), new SubjectTerm(subjectFilter.toUpperCase()));
                        SearchTerm term = new AndTerm(new AndTerm(dateTerm, fromTerm), subjTerm);

                        Message[] msgs = f.search(term);
                        if (msgs.length == 0) {
                            f.close(false);
                            continue;
                        }

                        Arrays.sort(msgs, (a, b) -> {
                            Date da = safeDate(a);
                            Date db = safeDate(b);
                            if (da == null && db == null) return 0;
                            if (da == null) return 1;
                            if (db == null) return -1;
                            return db.compareTo(da);
                        });

                        for (Message m : msgs) {
                            Date d = safeDate(m);
                            if (d != null && d.before(since)) continue;
                            if (!matchRecipientStrict(m, targetEmail)) continue;

                            String code = extract(read(m), m.getSubject());
                            if (code != null) {
                                if (deleteAfterRead) {
                                    m.setFlag(Flags.Flag.DELETED, true);
                                    f.close(true);
                                } else {
                                    f.close(false);
                                }
                                return code;
                            }
                        }
                    } finally {
                        if (f.isOpen()) f.close(false);
                    }
                }
                Thread.sleep(pollSec * 1000L);
            }
            return null;
        } finally {
            if (store.isConnected()) store.close();
        }
    }

    private Date safeDate(Message m) {
        try { return m.getReceivedDate() != null ? m.getReceivedDate() : m.getSentDate(); }
        catch (Exception e) { return null; }
    }

    private boolean matchRecipientStrict(Message m, String target) {
        try {
            String t = target.toLowerCase();
            if (t.contains("+")) {
                if (headerEquals(m, "Delivered-To", t)) return true;
                Address[] tos = m.getRecipients(Message.RecipientType.TO);
                if (tos == null) return false;
                for (Address a : tos) {
                    if (a instanceof InternetAddress) {
                        if (((InternetAddress) a).getAddress().equalsIgnoreCase(target)) return true;
                    }
                    if (a.toString().equalsIgnoreCase(target)) return true;
                }
                return false;
            }
            String normT = stripDots(t);
            if (headerEquals(m, "Delivered-To", normT)) return true;
            Address[] tos = m.getRecipients(Message.RecipientType.TO);
            if (tos == null) return true;
            for (Address a : tos) {
                String v = a.toString().toLowerCase();
                if (v.contains(t) || stripDots(v).contains(normT)) return true;
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    private boolean headerEquals(Message m, String name, String target) {
        try {
            String[] hs = m.getHeader(name);
            if (hs == null) return false;
            for (String h : hs) {
                if (h == null) continue;
                String val = h.trim().toLowerCase();
                if (val.equals(target) || val.equals(stripDots(target))) return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private String stripDots(String email) {
        int at = email.indexOf('@');
        if (at < 0) return email.replace(".", "");
        return email.substring(0, at).replace(".", "") + email.substring(at);
    }

    private String read(Message m) throws Exception {
        Object c = m.getContent();
        if (c instanceof String) return (String) c;
        if (c instanceof Multipart) {
            StringBuilder sb = new StringBuilder();
            Multipart mp = (Multipart) c;
            for (int i = 0; i < mp.getCount(); i++) {
                BodyPart bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) sb.append(bp.getContent());
                else if (bp.isMimeType("text/html")) sb.append(String.valueOf(bp.getContent()).replaceAll("<[^>]+>", " "));
                else if (bp.getContent() instanceof Multipart) {
                    Multipart in = (Multipart) bp.getContent();
                    for (int j = 0; j < in.getCount(); j++) {
                        BodyPart b = in.getBodyPart(j);
                        if (b.isMimeType("text/plain")) sb.append(b.getContent());
                        else if (b.isMimeType("text/html")) sb.append(String.valueOf(b.getContent()).replaceAll("<[^>]+>", " "));
                    }
                }
            }
            return sb.toString();
        }
        return "";
    }

    private String extract(String body, String subj) {
        if (body != null) {
            Matcher mb = codeRx.matcher(body);
            if (mb.find()) return mb.group(1);
        }
        if (subj != null) {
            Matcher ms = codeRx.matcher(subj);
            if (ms.find()) return ms.group(1);
        }
        return null;
    }
}