package com.github.adam6806.pnlanalyzer.domain;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "tbr_file")
public class TrialBalanceReport {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tbr_file_id")
    private Long id;

    @Column
    @NotEmpty(message = "Please provide a name")
    private String name;

    @OrderBy
    @Column
    @Temporal(TemporalType.DATE)
    private Date date;

    @OneToMany(mappedBy = "trialBalanceReport", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<LineItem> lineItems;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Set<LineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(Set<LineItem> lineItems) {
        for (LineItem lineItem : lineItems) {
            lineItem.setTrialBalanceReport(this);
        }
        this.lineItems = lineItems;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
