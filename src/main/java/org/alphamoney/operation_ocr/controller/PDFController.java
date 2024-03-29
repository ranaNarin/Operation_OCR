package org.alphamoney.operation_ocr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.alphamoney.operation_ocr.model.StatementInfo;
import org.alphamoney.operation_ocr.service.PdfService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.DecryptionMaterial;
import org.apache.pdfbox.pdmodel.encryption.StandardDecryptionMaterial;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api")
public class PDFController {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");


    private final PdfService pdfService;

    public PDFController(PdfService pdfService) {
        this.pdfService = pdfService;
    }


    @PostMapping("/upload/pdf")
    public ResponseEntity<String> uploadAndReadPDF(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String password) {
        try {
            // Check if the uploaded file is a PDF
            if (!file.getContentType().equalsIgnoreCase("application/pdf")) {
                return ResponseEntity.badRequest().body("Uploaded file is not a PDF");
            }

            // Load PDF document from the uploaded file
            try (InputStream inputStream = file.getInputStream()) {
                PDDocument document;
                if (password != null && !password.isEmpty()) {
                    document = PDDocument.load(inputStream, password);
                } else {
                    document = PDDocument.load(inputStream);
                }

                // Instantiate PDFTextStripper class
                PDFTextStripper pdfStripper = new PDFTextStripper();

                // Extract text from PDF
                String text = pdfStripper.getText(document);

                // Close the document
                document.close();
                // Perform text processing to extract required information
                String summary = extractSummary(text);
                String personalInfo = extractPersonalInfo(text);

//                double totalIncome = calculateTotalIncome(text);
//                double totalExpenditure = calculateTotalExpenditure(text);
//                double netIncome = totalIncome - totalExpenditure;
//
//                // Make loan approval decision based on net income
//                String approvalStatus = (netIncome > 0) ? "Approved" : "Rejected";

                String json = parseTextToJSON(text);
                return ResponseEntity.ok(json);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to read PDF: " + e.getMessage());
        }
    }

    // Example method to calculate total income
    private double calculateTotalIncome(String text) {
        // Implement logic to extract and sum up income data from text
        return 0.0;
    }

    // Example method to calculate total expenditure
    private double calculateTotalExpenditure(String text) {
        // Implement logic to extract and sum up expenditure data from text
        return 0.0;
    }

    private String extractSummary(String text) {
        // Initialize variables to store income, expenditure, and balance
        double income = 0.0;
        double expenditure = 0.0;
        double balance = 0.0;

        // Regular expressions to match patterns for income, expenditure, and balance
        Pattern incomePattern = Pattern.compile("Income:\\s*([0-9,.]+)");
        Pattern expenditurePattern = Pattern.compile("Expenditure:\\s*([0-9,.]+)");
        Pattern balancePattern = Pattern.compile("Balance:\\s*([0-9,.]+)");

        // Match income
        Matcher incomeMatcher = incomePattern.matcher(text);
        if (incomeMatcher.find()) {
            income = Double.parseDouble(incomeMatcher.group(1).replace(",", ""));
        }

        // Match expenditure
        Matcher expenditureMatcher = expenditurePattern.matcher(text);
        if (expenditureMatcher.find()) {
            expenditure = Double.parseDouble(expenditureMatcher.group(1).replace(",", ""));
        }

        // Match balance
        Matcher balanceMatcher = balancePattern.matcher(text);
        if (balanceMatcher.find()) {
            balance = Double.parseDouble(balanceMatcher.group(1).replace(",", ""));
        }

        // Create a summary string
        StringBuilder summary = new StringBuilder();
        summary.append("Income: ").append(income).append("\n");
        summary.append("Expenditure: ").append(expenditure).append("\n");
        summary.append("Balance: ").append(balance).append("\n");

        System.out.println("extractSummary.....Line 126..."+summary);
        return summary.toString();
    }

    private String extractPersonalInfo(String text) {
        // Initialize variables to store personal information
        String name = "";
        String address = "";
        String ifscCode = "";
        String micrCode = "";
        String mobileNumber = "";
        String email = "";
        String pan = "";
        String scheme = "";
        String accountNumber = "";

        // Regular expressions to match patterns for personal information
        Pattern namePattern = Pattern.compile("Name:\\s*(.*?)\\n");
        Pattern addressPattern = Pattern.compile("Address:\\s*(.*?)\\n");
        Pattern ifscPattern = Pattern.compile("IFSC Code:\\s*(\\w+)\\n");
        Pattern micrPattern = Pattern.compile("MICR Code:\\s*(\\w+)\\n");
        Pattern mobilePattern = Pattern.compile("Mobile No:\\s*(\\d+)\\n");
        Pattern emailPattern = Pattern.compile("Email ID:\\s*(.*?)\\n");
        Pattern panPattern = Pattern.compile("PAN:\\s*(\\w+)\\n");
        Pattern schemePattern = Pattern.compile("Scheme:\\s*(.*?)\\n");
        Pattern accountPattern = Pattern.compile("Account No:\\s*(\\w+)\\n");

        // Match personal information
        Matcher nameMatcher = namePattern.matcher(text);
        if (nameMatcher.find()) {
            name = nameMatcher.group(1).trim();
        }

        Matcher addressMatcher = addressPattern.matcher(text);
        if (addressMatcher.find()) {
            address = addressMatcher.group(1).trim();
        }

        Matcher ifscMatcher = ifscPattern.matcher(text);
        if (ifscMatcher.find()) {
            ifscCode = ifscMatcher.group(1).trim();
        }

        Matcher micrMatcher = micrPattern.matcher(text);
        if (micrMatcher.find()) {
            micrCode = micrMatcher.group(1).trim();
        }

        Matcher mobileMatcher = mobilePattern.matcher(text);
        if (mobileMatcher.find()) {
            mobileNumber = mobileMatcher.group(1).trim();
        }

        Matcher emailMatcher = emailPattern.matcher(text);
        if (emailMatcher.find()) {
            email = emailMatcher.group(1).trim();
        }

        Matcher panMatcher = panPattern.matcher(text);
        if (panMatcher.find()) {
            pan = panMatcher.group(1).trim();
        }

        Matcher schemeMatcher = schemePattern.matcher(text);
        if (schemeMatcher.find()) {
            scheme = schemeMatcher.group(1).trim();
        }

        Matcher accountMatcher = accountPattern.matcher(text);
        if (accountMatcher.find()) {
            accountNumber = accountMatcher.group(1).trim();
        }

        // Create a personal information summary string
        StringBuilder personalInfo = new StringBuilder();
        personalInfo.append("Name: ").append(name).append("\n");
        personalInfo.append("Address: ").append(address).append("\n");
        personalInfo.append("IFSC Code: ").append(ifscCode).append("\n");
        personalInfo.append("MICR Code: ").append(micrCode).append("\n");
        personalInfo.append("Mobile No: ").append(mobileNumber).append("\n");
        personalInfo.append("Email ID: ").append(email).append("\n");
        personalInfo.append("PAN: ").append(pan).append("\n");
        personalInfo.append("Scheme: ").append(scheme).append("\n");
        personalInfo.append("Account No: ").append(accountNumber).append("\n");


        System.out.println("extractPersonalInfo.....Line 212..."+personalInfo);
        return personalInfo.toString();
    }

    private static String parseTextToJSON(String text) {
        String[] lines = text.split("\n");
        Map<String, Object> data = new HashMap<>();
        List<Map<String, String>> transactions = new ArrayList<>();
        Map<String, String> accountInfo = new HashMap<>();

        // Extract account holder information
        accountInfo.put("Account Holder", lines[0].trim());

        // Extract address information
        StringBuilder address = new StringBuilder();
        for (int i = 2; i < 5; i++) {
            address.append(lines[i].trim()).append(", ");
        }
        accountInfo.put("Address", address.toString().trim());

        // Extract other account-related information
        for (int i = 5; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.startsWith("Statement of Account")) {
                // Extract statement period
                String[] parts = line.split("for the period \\(From : | To : |\\)");
                try {
                    Date fromDate = DATE_FORMAT.parse(parts[1]);
                    Date toDate = DATE_FORMAT.parse(parts[2]);
                    accountInfo.put("Statement Period - From", DATE_FORMAT.format(fromDate));
                    accountInfo.put("Statement Period - To", DATE_FORMAT.format(toDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (line.matches("\\d{2}-\\d{2}-\\d{4}.*")) {
                // Extract transaction details
                String[] transactionParts = line.split("\\s{2,}");
                Map<String, String> transaction = new HashMap<>();
                try {
                    if (transactionParts.length >= 5) { // Check if transactionParts has enough elements
                        transaction.put("Date", DATE_FORMAT.format(DATE_FORMAT.parse(transactionParts[0])));
                        transaction.put("Particulars", transactionParts[1]);
                        transaction.put("Debit", transactionParts[2].trim());
                        transaction.put("Credit", transactionParts[3].trim());
                        transaction.put("Balance", transactionParts[4].trim());
                    } else {
                        // Handle case where transactionParts doesn't have enough elements
                        // Log an error or throw an exception
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                transactions.add(transaction);
            } else if (line.startsWith("Customer ID") || line.startsWith("IFSC Code") ||
                    line.startsWith("MICR Code") || line.startsWith("Nominee Registered") ||
                    line.startsWith("Registered Mobile No") || line.startsWith("Registered Email ID") ||
                    line.startsWith("PAN") || line.startsWith("Scheme") || line.startsWith("Statement of Account No")) {
                // Other account-related information
                String[] parts = line.split(":", 2);
                accountInfo.put(parts[0].trim(), parts[1].trim());
            }
        }

        // Add transactions to data
        data.put("Account Information", accountInfo);
        data.put("Transactions", transactions);

        // Convert data map to JSON
        try {
            return new ObjectMapper().writeValueAsString(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}