package com.github.adam6806.pnlanalyzer.trialbalancereport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Adam on 6/17/2017.
 */
class DifferenceCalculator {

    static List<LineItem> calculateDifference(Set<LineItem> previousLineItems, Set<LineItem> currentLineItems) {

        List<LineItem> newLineItems = new ArrayList<>();
        Iterator<LineItem> currentIterator = currentLineItems.iterator();
        LineItem newTotalLineItem = new LineItem();
        newTotalLineItem.setDescription("Total");
        Double totalDebit = 0.0;
        Double totalCredit = 0.0;
        while (currentIterator.hasNext()) {
            LineItem currentLineItem = currentIterator.next();
            Iterator<LineItem> previousIterator = previousLineItems.iterator();
            boolean lineItemNotFound = true;
            while (previousIterator.hasNext()) {
                LineItem previousLineItem = previousIterator.next();
                if (currentLineItem.equals(previousLineItem)) {
                    LineItem newLineItem = currentLineItem.minus(previousLineItem);
                    totalDebit += newLineItem.getDebit();
                    totalCredit += newLineItem.getCredit();
                    newLineItems.add(newLineItem);
                    previousIterator.remove();
                    lineItemNotFound = false;
                    break;
                }
            }
            if (lineItemNotFound) {
                newLineItems.add(currentLineItem);
                totalDebit += currentLineItem.getDebit();
                totalCredit += currentLineItem.getCredit();
            }
        }

        newTotalLineItem.setDebit(totalDebit);
        newTotalLineItem.setCredit(totalCredit);
        newLineItems.add(newTotalLineItem);

        for (LineItem previousLineItem : previousLineItems) {
            previousLineItem.setDescription(previousLineItem.getDescription() + " **WAS NOT IN CURRENT FILE**");
        }
        return newLineItems;
    }
}