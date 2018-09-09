package com.github.adam6806.pnlanalyzer.services;

import com.github.adam6806.pnlanalyzer.domain.LineItem;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Adam on 6/17/2017.
 */
@Service
public class DifferenceCalculatorService {

    public List<LineItem> calculateDifference(Set<LineItem> previousLineItems, Set<LineItem> currentLineItems) {

        List<String> wantedAccounts = Arrays.asList("5060", "5070", "1210", "1215");
        List<LineItem> newLineItems = new ArrayList<>();
        Iterator<LineItem> currentIterator = currentLineItems.iterator();
        LineItem newTotalLineItem = new LineItem();
        newTotalLineItem.setDescription("Total");
        Double totalDebit = 0.0;
        Double totalCredit = 0.0;
        Double total4000Debit = 0.0;
        Double total4000Credit = 0.0;
        while (currentIterator.hasNext()) {
            LineItem currentLineItem = currentIterator.next();
            if (currentLineItem.getDescription().startsWith("4") || wantedAccounts.parallelStream().anyMatch(currentLineItem.getDescription()::contains)) {
                Iterator<LineItem> previousIterator = previousLineItems.iterator();
                boolean lineItemNotFound = true;
                while (previousIterator.hasNext()) {
                    LineItem previousLineItem = previousIterator.next();
                    if (currentLineItem.equals(previousLineItem)) {
                        LineItem newLineItem = currentLineItem.minus(previousLineItem);
                        if (newLineItem.getDescription().startsWith("4")) {
                            total4000Credit += newLineItem.getCredit();
                            total4000Debit += newLineItem.getDebit();
                        }
                        totalDebit += newLineItem.getDebit();
                        totalCredit += newLineItem.getCredit();
                        newLineItems.add(newLineItem);
                        previousIterator.remove();
                        lineItemNotFound = false;
                        break;
                    }
                }
                if (lineItemNotFound) {
                    if (currentLineItem.getDescription().startsWith("4")) {
                        total4000Credit += currentLineItem.getCredit();
                        total4000Debit += currentLineItem.getDebit();
                    }
                    newLineItems.add(currentLineItem);
                    totalDebit += currentLineItem.getDebit();
                    totalCredit += currentLineItem.getCredit();
                }
            }
        }

        LineItem incomeClearing = new LineItem().setCredit(total4000Debit).setDebit(total4000Credit).setDescription("1060 · Income Clearing");
        newLineItems.add(incomeClearing);
        newTotalLineItem.setDebit(totalDebit + total4000Credit);
        newTotalLineItem.setCredit(totalCredit + total4000Debit);
        newLineItems.add(newTotalLineItem);

        return newLineItems;
    }
}