package com.github.adam6806.pnlanalyzer.entities;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tbr_file_id")
    private TrialBalanceReport trialBalanceReport;

    public LineItem() {
        description = "";
        credit = 0.0;
        debit = 0.0;
    }

    public Long getId() {
        return id;
    }

    public LineItem setId(Long id) {
        this.id = id;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public LineItem setDescription(String description) {
        this.description = description;
        return this;
    }

    public Double getCredit() {
        return credit;
    }

    public LineItem setCredit(Double credit) {
        this.credit = credit;
        return this;
    }

    public Double getDebit() {
        return debit;
    }

    public LineItem setDebit(Double debit) {
        this.debit = debit;
        return this;
    }

    public TrialBalanceReport getTrialBalanceReport() {
        return trialBalanceReport;
    }

    public LineItem setTrialBalanceReport(TrialBalanceReport trialBalanceReport) {
        this.trialBalanceReport = trialBalanceReport;
        return this;
    }

    public String getCreditString() {
        return decimalFormat.format(getCredit());
    }

    public String getDebitString() {
        return decimalFormat.format(getDebit());
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
