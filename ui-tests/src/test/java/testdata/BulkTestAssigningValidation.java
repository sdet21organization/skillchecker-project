package testdata;

public enum BulkTestAssigningValidation {

    TESTINFO1("Сказки", "Описание: Проверка знаний сказок", "Время выполнения: 30 мин.", "Проходной балл: 70%");

    public final String name;
    public final String description;
    public final String executiontime;
    public final String passingscore;

    BulkTestAssigningValidation(String name, String description, String executiontime, String passingscore) {
        this.name = name;
        this.description = description;
        this.executiontime = executiontime;
        this.passingscore = passingscore;
    }
}
