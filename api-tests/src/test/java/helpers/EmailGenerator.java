package helpers;

public class EmailGenerator {
    public static String generateUnique() {
        return "skillchekerde+" + System.currentTimeMillis() + "@gmail.com";
    }
}