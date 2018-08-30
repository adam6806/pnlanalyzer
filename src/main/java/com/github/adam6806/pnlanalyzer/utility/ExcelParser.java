package com.github.adam6806.pnlanalyzer.utility;

import com.github.adam6806.pnlanalyzer.entities.LineItem;
import com.github.adam6806.pnlanalyzer.entities.TrialBalanceReport;
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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ExcelParser {

    public static List<LineItem> parseExcelFile(InputStream excelFile) throws IOException, InvalidFormatException {

        List<LineItem> lineItemList = new ArrayList<>();

        Workbook workbook = WorkbookFactory.create(excelFile);
        Sheet sheet1 = workbook.getSheetAt(0);
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if ("Sheet1".equals(sheet.getSheetName())) {
                sheet1 = sheet;
                break;
            }
        }

        int debitColumn = -1;
        int creditColumn = -1;

        for (Row row : sheet1) {
            if (row == null) continue;
            LineItem lineItem = new LineItem();
            boolean isLineItem = false;
            for (Cell cell : row) {
                if (debitColumn == -1 && "Debit".equalsIgnoreCase(cell.getStringCellValue())) {
                    debitColumn = cell.getColumnIndex();
                } else if (creditColumn == -1 && "Credit".equalsIgnoreCase(cell.getStringCellValue())) {
                    creditColumn = cell.getColumnIndex();
                } else if (cell.getAddress().getRow() == 0 && cell.getAddress().getColumn() == 2) {
                    lineItem.setDescription(cell.getStringCellValue());
                } else if (cell.getAddress().getColumn() == 1 && !cell.getStringCellValue().isEmpty()) {
                    isLineItem = true;
                    lineItem.setDescription(cell.getStringCellValue());
                } else if (cell.getAddress().getColumn() == debitColumn && isLineItem) {
                    lineItem.setDebit(cell.getNumericCellValue());
                } else if (cell.getAddress().getColumn() == creditColumn && isLineItem) {
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

        double total = 0.0;
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        lineItems.sort(Comparator.comparing(LineItem::getDescription));

        List<String> wantedAccounts = Arrays.asList("5060", "5070", "1210", "1215");

        boolean isFirst = true;
        for (LineItem lineItem : lineItems) {
            if (lineItem.getDescription().startsWith("4") || wantedAccounts.parallelStream().anyMatch(lineItem.getDescription()::contains)) {
                String firstColumn = "SPL";
                if (isFirst) {
                    isFirst = false;
                    firstColumn = "TRNS";
                }

                Double amount = lineItem.getDebit() - lineItem.getCredit();

                if (lineItem.getDescription().startsWith("4")) {
                    total += amount;
                }

                csvPrinter.printRecord(firstColumn, "", "GENERAL JOURNAL", simpleDateFormat.format(current.getDate()), lineItem.getDescription(), "", decimalFormat.format(amount), "", "");
            }
        }
        String incomeClearing = new String("1060 · Income Clearing".getBytes());
        csvPrinter.printRecord("SPL", "", "GENERAL JOURNAL", simpleDateFormat.format(current.getDate()), incomeClearing, "", decimalFormat.format(0 - total), "", "");
        csvPrinter.printRecord("ENDTRNS", "", "", "", "", "", "", "", "");

        byte badByte = (byte) ("Â".getBytes()[0] - 1);
        byte[] bytes = csvPrinter.getOut().toString().getBytes();
        List<Byte> byteList = new ArrayList<>();
        for (byte aByte : bytes) {
            if (aByte != badByte) {
                byteList.add(aByte);
            }
        }
        byte[] goodBytes = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            goodBytes[i] = byteList.get(i);
        }

        return new ByteArrayInputStream(goodBytes);
    }
}
