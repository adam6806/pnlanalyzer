package com.github.adam6806.pnlanalyzer.trialbalancereport;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ExcelParser {

    public static List<LineItem> parseExcelFile(InputStream excelFile) throws IOException, InvalidFormatException {

        List<LineItem> lineItemList = new ArrayList();

        Workbook workbook = WorkbookFactory.create(excelFile);
        Sheet sheet = workbook.getSheetAt(0);
        for (Row row : sheet) {
            if (row == null) continue;
            LineItem lineItem = new LineItem();
            boolean isLineItem = false;
            for (Cell cell : row) {
                if (cell.getAddress().getRow() == 0 && cell.getAddress().getColumn() == 2) {
                    lineItem.setDescription(cell.getStringCellValue());
                } else if (cell.getAddress().getColumn() == 1 && !cell.getStringCellValue().isEmpty()) {
                    isLineItem = true;
                    lineItem.setDescription(cell.getStringCellValue());
                } else if (cell.getAddress().getColumn() == 2 && isLineItem) {
                    lineItem.setDebit(cell.getNumericCellValue());
                } else if (cell.getAddress().getColumn() == 4 && isLineItem) {
                    lineItem.setCredit(cell.getNumericCellValue());
                }
            }
            String description = lineItem.getDescription().trim();
            if (!description.isEmpty()) {
                lineItemList.add(lineItem);
            }
        }
        return lineItemList;
    }

    public static InputStream generateJournalEntries(List<LineItem> lineItems, TrialBalanceReport current) throws IOException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M/d/yyyy");

        StringBuilder stringBuilder = new StringBuilder();
        CSVPrinter csvPrinter = new CSVPrinter(stringBuilder, CSVFormat.TDF.withEscape('\\').withQuoteMode(QuoteMode.NONE));
        csvPrinter.printRecord("!TRNS", "TRNSID", "TRNSTYPE", "DATE", "ACCNT", "CLASS", "AMOUNT", "DOCNUM", "MEMO");
        csvPrinter.printRecord("!SPL", "SPLID", "TRNSTYPE", "DATE", "ACCNT", "CLASS", "AMOUNT", "DOCNUM", "MEMO");
        csvPrinter.printRecord("!ENDTRNS", "", "", "", "", "", "", "", "");

        boolean isFirst = true;
        for (int i = 0; i < lineItems.size(); i++) {
            LineItem lineItem = lineItems.get(i);
            if (lineItem.getDescription().startsWith("4")) {
                String firstColumn = "SPL";
                if (isFirst) {
                    isFirst = false;
                    firstColumn = "TRNS";
                }
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                Double amount = lineItem.getDebit() - lineItem.getCredit();
                csvPrinter.printRecord(firstColumn, "", "GENERAL JOURNAL", simpleDateFormat.format(current.getDate()), lineItem.getDescription(), "", decimalFormat.format(amount), "", "");
            }
        }

        csvPrinter.printRecord("ENDTRNS", "", "", "", "", "", "", "", "");

        return new ByteArrayInputStream(csvPrinter.getOut().toString().getBytes());
    }
}
