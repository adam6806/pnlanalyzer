package com.github.adam6806.pnlanalyzer.services;

import com.github.adam6806.pnlanalyzer.domain.LineItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * Adam on 6/17/2017.
 */
@Service
public class DifferenceCalculatorService {

    public List<LineItem> calculateDifference(Set<LineItem> previousLineItems, Set<LineItem> currentLineItems) {

        List<String> wantedAccounts = Arrays.asList();
        List<LineItem> newLineItems = new ArrayList<>();
        Iterator<LineItem> currentIterator = currentLineItems.iterator();
        LineItem newTotalLineItem = new LineItem();
        newTotalLineItem.setDescription("Total");
        BigDecimal totalDebit = BigDecimal.valueOf(0.0);
        BigDecimal totalCredit = BigDecimal.valueOf(0.0);
        BigDecimal total4000Debit = BigDecimal.valueOf(0.0);
        BigDecimal total4000Credit = BigDecimal.valueOf(0.0);
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
                            total4000Credit = total4000Credit.add(newLineItem.getCredit());
                            total4000Debit = total4000Debit.add(newLineItem.getDebit());
                        }
                        totalDebit = totalDebit.add(newLineItem.getDebit());
                        totalCredit = totalCredit.add(newLineItem.getCredit());
                        newLineItems.add(newLineItem);
                        previousIterator.remove();
                        lineItemNotFound = false;
                        break;
                    }
                }
                if (lineItemNotFound) {
                    if (currentLineItem.getDescription().startsWith("4")) {
                        total4000Credit = total4000Credit.add(currentLineItem.getCredit());
                        total4000Debit = total4000Debit.add(currentLineItem.getDebit());
                    }
                    newLineItems.add(currentLineItem);
                    totalDebit = totalDebit.add(currentLineItem.getDebit());
                    totalCredit = totalCredit.add(currentLineItem.getCredit());
                }
            }
        }

        LineItem incomeClearing = new LineItem().setCredit(total4000Debit).setDebit(total4000Credit).setDescription("1060 · Income Clearing");
        newLineItems.add(incomeClearing);
        newTotalLineItem.setDebit(totalDebit.add(total4000Credit));
        newTotalLineItem.setCredit(totalCredit.add(total4000Debit));
        newLineItems.add(newTotalLineItem);

        return newLineItems;
    }
}