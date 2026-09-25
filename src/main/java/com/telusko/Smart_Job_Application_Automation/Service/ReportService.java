package com.telusko.Smart_Job_Application_Automation.Service;

import com.telusko.Smart_Job_Application_Automation.Model.Recruiter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ReportService {
    public void generateReport(List<Recruiter> recruiters){
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Email Report");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Recruiter");
        header.createCell(1).setCellValue("Company");
        header.createCell(2).setCellValue("Email");
        header.createCell(3).setCellValue("Status");
        header.createCell(4).setCellValue("Sent Time");

        int rowNum = 1;

        for (Recruiter recruiter : recruiters) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(recruiter.getFirstName());
            row.createCell(1).setCellValue(recruiter.getCompany());
            row.createCell(2).setCellValue(recruiter.getPosition());
            row.createCell(3).setCellValue(recruiter.getEmail());
            row.createCell(4).setCellValue(recruiter.getStatus() == null ? "NOT SENT" : recruiter.getStatus());
            row.createCell(5).setCellValue(recruiter.getSentTime() == null ? "" : recruiter.getSentTime().toString());
        }


        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }
        try {
            FileOutputStream fos = new FileOutputStream("EmailReport.xlsx");
            workbook.write(fos);
            fos.close();
            workbook.close();
            System.out.println("Report Generated Successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
