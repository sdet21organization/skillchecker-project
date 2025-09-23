package dto.manage;

public class CreateTestRequest {
    private String name;
    private String description;

    public CreateTestRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}

