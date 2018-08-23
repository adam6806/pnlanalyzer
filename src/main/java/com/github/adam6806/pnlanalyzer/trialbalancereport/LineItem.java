package com.github.adam6806.pnlanalyzer.trialbalancereport;

import javax.persistence.*;
import java.text.DecimalFormat;

/**
 * Adam on 6/17/2017.
 */
@Entity
@Table(name = "line_item")
public class LineItem {

    @Transient
    private static DecimalFormat decimalFormat = new DecimalFormat("$###,###.##");
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "line_item_id")
    private Long id;
    @Column
    private String description;
    @Column
    private Double credit;
    @Column
    private Double debit;

    public LineItem() {
        description = "";
        credit = 0.0;
        debit = 0.0;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    public String getCreditString() {
        return decimalFormat.format(getCredit());
    }

    public Double getDebit() {
        return debit;
    }

    public void setDebit(Double debit) {
        this.debit = debit;
    }

    public String getDebitString() {
        return decimalFormat.format(getDebit());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean equals(LineItem lineItem) {
        return getDescription().equalsIgnoreCase(lineItem.getDescription());
    }

    public LineItem minus(LineItem prevLineItem) {
        LineItem result = new LineItem();
        result.setDescription(getDescription());
        result.setCredit(getCredit() - prevLineItem.getCredit());
        result.setDebit(getDebit() - prevLineItem.getDebit());
        return result;
    }
}
