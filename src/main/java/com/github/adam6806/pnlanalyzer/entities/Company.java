package com.github.adam6806.pnlanalyzer.entities;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Entity
@Table(name = "company")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "company_id")
    private Long id;

    @OrderBy
    @Column(name = "name", unique = true)
    @NotEmpty(message = "*Please provide the company name")
    private String name;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TrialBalanceReport> trialBalanceReports;

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

    public Set<TrialBalanceReport> getTrialBalanceReports() {
        return trialBalanceReports;
    }

    public void setTrialBalanceReports(Set<TrialBalanceReport> trialBalanceReports) {
        for (TrialBalanceReport trialBalanceReport : trialBalanceReports) {
            trialBalanceReport.setCompany(this);
        }
        this.trialBalanceReports = trialBalanceReports;
    }
}
