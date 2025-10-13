package helpers;

public class EmailGenerator {
    public static String generateUnique() {
        String base = "skillchekerde";
        String domain = "gmail.com";

        int pos = 1 + (int) (Math.random() * (base.length() - 1));
        String modified = base.substring(0, pos) + "." + base.substring(pos);

        return modified + "@" + domain;
    }
}