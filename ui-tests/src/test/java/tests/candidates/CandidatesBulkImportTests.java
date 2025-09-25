package tests.candidates;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.CandidatesPage;
import tests.BaseTest;
import utils.ExcelGenerator;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Масовый импорт кандидатов")
public class CandidatesBulkImportTests extends BaseTest {

    @Test
    @DisplayName("Успешний импорт кандидатов из Excel")
    public void successfulImportCandidatesTest() throws IOException {
        CandidatesPage candidatesPage = new CandidatesPage(context);

        String name1 = "Джон Иванов" + LocalDateTime.now();
        String name2 = "Питр Петров" + LocalDateTime.now();
        String expectedImportInfoStatus = "Успешно импортировано: 2";

        List<ExcelGenerator.Candidate> candidates = List.of(new ExcelGenerator.Candidate("johnivanov" + LocalDateTime.now() + "@example.com", name1, "Developer"), new ExcelGenerator.Candidate("piterpetrov" + LocalDateTime.now() + "@example.com", name2, "QA Engineer"));

        String filePath = ExcelGenerator.generateExcel(candidates, "target/import/candidates.xlsx");

        candidatesPage.open().clickImportButton();
        candidatesPage.fileUploadImportModalButton.setInputFiles(Paths.get(filePath));
        candidatesPage.clickSubmitImportButton();
        assertEquals(expectedImportInfoStatus, candidatesPage.importInfoStatus.textContent(), "Сообщение о статусе импорта не совпадает с ожидаемым");

        candidatesPage.clickImportCancelButton();

        candidatesPage.searchCandidateBy(name1);
        assertEquals(name1, candidatesPage.candidatesTableNames.textContent(), "Не найдено импортированого из файла кандидата");

        candidatesPage.searchCandidateBy(name2);
        assertEquals(name2, candidatesPage.candidatesTableNames.textContent(), "Не найдено импортированого из файла кандидата");
    }

    @Test
    @DisplayName("Неуспешный импорт кандидатов из Excel: отсутсвует имя, некоректный email")
    public void unsuccessfulImportCandidatesTest() throws IOException {
        CandidatesPage candidatesPage = new CandidatesPage(context);

        String name1 = "";
        String name2 = "Питр Петров" + LocalDateTime.now();
        String expectedImportInfoStatus = "Успешно импортировано: 0Ошибки (2):Строка 2: Name - Обязательное поле отсутствует или имеет неверный форматСтрока 3: Email - Неверный формат email адреса";

        List<ExcelGenerator.Candidate> candidates = List.of(new ExcelGenerator.Candidate("johnivanov" + LocalDateTime.now() + "@example.com", name1, "Developer"), new ExcelGenerator.Candidate("piterpetrov" + LocalDateTime.now() + "example.com", name2, "QA Engineer"));

        String filePath = ExcelGenerator.generateExcel(candidates, "target/import/candidates.xlsx");

        candidatesPage.open().clickImportButton();
        candidatesPage.fileUploadImportModalButton.setInputFiles(Paths.get(filePath));
        candidatesPage.clickSubmitImportButton();
        assertEquals(expectedImportInfoStatus, candidatesPage.importInfoStatus.textContent(), "Сообщение о статусе импорта не совпадает с ожидаемым");

        candidatesPage.clickImportCancelButton();
    }
}