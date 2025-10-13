package tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import utils.ConfigurationReader;

public abstract class BaseUiTest extends BaseTest {

    public static final String DEF_NAME = "Test User";
    public static final String DEF_ORG  = "SkillChecker QA";
    public static final String DEF_PWD  = "Qq!12345";

    protected String uniqueEmail() {
        String inbox = ConfigurationReader.get("mail.username");
        if (inbox != null && inbox.endsWith("@gmail.com")) {
            int at = inbox.indexOf('@');
            String local = inbox.substring(0, at).replace(".", "");
            String domain = inbox.substring(at);
            long bits = System.currentTimeMillis();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < local.length(); i++) {
                sb.append(local.charAt(i));
                if (i < local.length() - 1 && ((bits >> i) & 1) == 1) {
                    sb.append('.');
                }
            }
            return sb.toString().toLowerCase() + domain;
        }
        String ts = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        String nano = Long.toString(System.nanoTime());
        String tail = nano.substring(nano.length() - 5);
        return ("qa_user_" + ts + "_" + tail + "@test.com").toLowerCase(java.util.Locale.ROOT);
    }

    @Override
    protected boolean needAuthCookie() {
        return false;
    }

    @Override
    @BeforeEach
    public void beforeEach() {
        super.beforeEach();
        String url = ConfigurationReader.get("URL");
        context.page.navigate(url);
    }

    @AfterEach
    public void afterEach() {
        super.afterEach();
    }
}