package com.telusko.Smart_Job_Application_Automation.Service;

import com.telusko.Smart_Job_Application_Automation.Model.Recruiter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelService {

    public List<Recruiter> readRecruiters(
            MultipartFile file
    ) throws Exception {

        List<Recruiter> recruiters =
                new ArrayList<>();
        try (
                InputStream inputStream =
                        file.getInputStream();

                Workbook workbook =
                        new XSSFWorkbook(inputStream)
        ) {

            Sheet sheet =
                    workbook.getSheetAt(0);

            for (
                    int i = 1;
                    i <= sheet.getLastRowNum();
                    i++
            ) {
                Row row =
                        sheet.getRow(i);

                if (row == null) {
                    continue;
                }

                String firstName =
                        getCellValue(
                                row.getCell(0)
                        );
                String email =
                        getCellValue(
                                row.getCell(1)
                        );
                String company =
                        getCellValue(
                                row.getCell(2)
                        );
                String position =
                        getCellValue(
                                row.getCell(3)
                        );



                if (email == null ||
                        email.isBlank()) {

                    continue;
                }

                Recruiter recruiter =
                        new Recruiter();


                recruiter.setFirstName(
                        firstName
                );


                recruiter.setEmail(
                        email
                );
                recruiter.setCompany(
                        company
                );
                recruiter.setPosition(
                        position
                );

                recruiter.setStatus(
                        "PENDING"
                );
                recruiter.setSentTime(
                        null
                );
                recruiter.setActive(
                        true
                );
                recruiters.add(
                        recruiter
                );
            }
        }


        return recruiters;
    }


    private String getCellValue(
            Cell cell
    ) {

        if (cell == null) {
            return "";
        }


        DataFormatter formatter =
                new DataFormatter();


        return formatter
                .formatCellValue(cell)
                .trim();
    }
}