package com.github.adam6806.pnlanalyzer.trialbalancereport;

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

    public static InputStream generateJournalEntries(List<LineItem> lineItems, TrialBalanceReport current) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M/d/yyyy");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("!TRNS\tTRNSID\tTRNSTYPE\tDATE\tACCNT\tCLASS\tAMOUNT\tDOCNUM\tMEMO\n");
        stringBuilder.append("!SPL\tSPLID\tTRNSTYPE\tDATE\tACCNT\tCLASS\tAMOUNT\tDOCNUM\tMEMO\n");
        stringBuilder.append("!ENDTRNS\t\t\t\t\t\t\t\t\n");

        boolean isFirst = true;
        for (int i = 0; i < lineItems.size(); i++) {
            LineItem lineItem = lineItems.get(i);
            if (lineItem.getDescription().startsWith("4")) {
                if (isFirst) {
                    isFirst = false;
                    stringBuilder.append("TRNS\t\tGENERAL JOURNAL\t");
                } else {
                    stringBuilder.append("SPL\t\tGENERAL JOURNAL\t");
                }
                stringBuilder.append(simpleDateFormat.format(current.getDate())).append("\t");
                stringBuilder.append(lineItem.getDescription()).append("\t\t");

                DecimalFormat decimalFormat = new DecimalFormat("###.##");
                Double amount = lineItem.getDebit() - lineItem.getCredit();
                stringBuilder.append(decimalFormat.format(amount)).append("\t\t\n");
            }
        }

        stringBuilder.append("ENDTRNS\t\t\t\t\t\t\t\t");

        return new ByteArrayInputStream(stringBuilder.toString().getBytes());
    }
}
