package com.github.adam6806.pnlanalyzer.services;

import com.github.adam6806.pnlanalyzer.entities.Company;

import java.util.List;

public interface CompanyService {

    List<Company> findAllCompanies();

    Company findCompanyById(Long companyId);

    void deleteCompanyById(Long companyId);

    void saveCompany(Company company);
}
