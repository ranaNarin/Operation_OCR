package org.alphamoney.operation_ocr.service;

import org.alphamoney.operation_ocr.model.StatementInfo;
import org.springframework.stereotype.Service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PdfService {

    public StatementInfo extractInfoFromPdf(MultipartFile file) {
        StatementInfo statementInfo = new StatementInfo();

        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            // Here you'll implement the logic to extract the information
            // This is just a placeholder implementation
            statementInfo.setSalary(extractSalary(text));
            statementInfo.setTotalCredits(extractTotalCredits(text));
            statementInfo.setTotalDebits(extractTotalDebits(text));

        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception or throw a custom one
        }

        return statementInfo;
    }

    private double extractSalary(String text) {
        // Example pattern, looks for the word "Salary" followed by an amount
        Pattern pattern = Pattern.compile("Salary\\s+\\₹?(\\d+[,.]?\\d*\\.\\d{2})");
        Matcher matcher = pattern.matcher(text);

        double salary = 0;
        while (matcher.find()) {
            // Assuming the first found amount after "Salary" is the monthly salary
            // This might need adjustment
            salary += Double.parseDouble(matcher.group(1).replace(",", ""));
            break; // Break after the first match if only one salary entry is expected per statement
        }


        return salary;
    }

    private double extractTotalCredits(String text) {
        // This is a simplistic example; you need to define what constitutes a "credit"
        double totalCredits = 0;
        // Example pattern, adjust as needed
        Pattern pattern = Pattern.compile("Credit\\s+\\₹?(\\d+[,.]?\\d*\\.\\d{2})");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            totalCredits += Double.parseDouble(matcher.group(1).replace(",", ""));
        }
        return totalCredits;
    }

    private double extractTotalDebits(String text) {
        // Similar to credits, define what you consider a debit
        double totalDebits = 0;
        // Example pattern, adjust as needed
        Pattern pattern = Pattern.compile("Debit\\s+\\₹?(\\d+[,.]?\\d*\\.\\d{2})");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            totalDebits += Double.parseDouble(matcher.group(1).replace(",", ""));
        }
        return totalDebits;
    }
}