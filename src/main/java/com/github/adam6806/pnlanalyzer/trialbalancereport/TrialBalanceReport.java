package com.github.adam6806.pnlanalyzer.trialbalancereport;

import com.github.adam6806.pnlanalyzer.company.Company;

import javax.persistence.*;
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
    private String name;
    @Column
    @Temporal(TemporalType.DATE)
    private Date date;
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "tbr_file_id")
    private Set<LineItem> lineItems;
    @ManyToOne
    @JoinColumn(name = "company_id")
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
        this.lineItems = lineItems;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
