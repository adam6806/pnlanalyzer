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

    @Column(name = "name")
    @NotEmpty(message = "*Please provide the company name")
    private String name;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "company_id")
    private Set<TrialBalanceReport> trialBalanceReport;

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

    public Set<TrialBalanceReport> getTrialBalanceReport() {
        return trialBalanceReport;
    }

    public void setTrialBalanceReport(Set<TrialBalanceReport> trialBalanceReport) {
        this.trialBalanceReport = trialBalanceReport;
    }
}
