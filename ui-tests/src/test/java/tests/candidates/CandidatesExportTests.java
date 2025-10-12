package tests.candidates;

import com.microsoft.playwright.Download;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.CandidatesPage;
import tests.BaseTest;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@DisplayName("Export of candidates")
public class CandidatesExportTests extends BaseTest {


    @Test
    @DisplayName("Check export of candidates")
    public void testExportCandidates() throws Exception {
        CandidatesPage candidatesPage = new CandidatesPage(context);
        candidatesPage.open();

        Download download = candidatesPage.exportCandidates();

        Path savePath = Paths.get("downloads", download.suggestedFilename());
        download.saveAs(savePath);

        Assertions.assertTrue(Files.exists(savePath), "File wasn't found");

        try (FileInputStream fis = new FileInputStream(savePath.toFile());
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            String col1 = headerRow.getCell(0).getStringCellValue();
            String col2 = headerRow.getCell(1).getStringCellValue();
            String col3 = headerRow.getCell(2).getStringCellValue();
            String col4 = headerRow.getCell(3).getStringCellValue();
            String col5 = headerRow.getCell(4).getStringCellValue();
            String col6 = headerRow.getCell(5).getStringCellValue();
            String col7 = headerRow.getCell(6).getStringCellValue();
            String col8 = headerRow.getCell(7).getStringCellValue();
            String col9 = headerRow.getCell(8).getStringCellValue();
            String col10 = headerRow.getCell(9).getStringCellValue();
            String col11 = headerRow.getCell(10).getStringCellValue();

            Assertions.assertEquals("Email", col1, "1st header is incorrect");
            Assertions.assertEquals("Имя", col2, "2d header is incorrect");
            Assertions.assertEquals("Должность", col3, "3d header is incorrect");
            Assertions.assertEquals("Дата добавления", col4, "4th header is incorrect");
            Assertions.assertEquals("Название теста", col5, "5th header is incorrect");
            Assertions.assertEquals("Дата назначения", col6, "6th header is incorrect");
            Assertions.assertEquals("Дата начала", col7, "7th header is incorrect");
            Assertions.assertEquals("Дата окончания", col8, "8th header is incorrect");
            Assertions.assertEquals("Статус", col9, "8th header is incorrect");
            Assertions.assertEquals("Баллы", col10, "8th header is incorrect");
            Assertions.assertEquals("Порог прохождения", col11, "8th header is incorrect");
        }
    }
}