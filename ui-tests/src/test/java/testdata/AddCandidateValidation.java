package testdata;

public enum AddCandidateValidation {
    MISSING_NAME("", "test@example.com", "Name must be at least 3 characters"),
    SHORT_NAME("", "test@example.com", "Name must be at least 3 characters"),
    MISSING_EMAIL("Test", "", "Invalid email address");

    private final String name;
    private final String email;
    private final String expectedError;

    AddCandidateValidation(String name, String email, String expectedError) {
        this.name = name;
        this.email = email;
        this.expectedError = expectedError;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getExpectedError() { return expectedError; }

}
