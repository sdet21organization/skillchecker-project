package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelGenerator {

    public static class Candidate {
        String email;
        String name;
        String position;

        public Candidate(String email, String name, String position) {
            this.email = email;
            this.name = name;
            this.position = position;
        }
    }

    public static String generateExcel(List<Candidate> candidates, String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Candidates");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Email");
        header.createCell(1).setCellValue("Name");
        header.createCell(2).setCellValue("Position");


        int rowIndex = 1;
        for (Candidate c : candidates) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(c.email);
            row.createCell(1).setCellValue(c.name);
            row.createCell(2).setCellValue(c.position);
        }

        File file = new File(filePath);
        file.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
        }
        workbook.close();

        return file.getAbsolutePath();
    }
}