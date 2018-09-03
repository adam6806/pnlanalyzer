package com.github.adam6806.pnlanalyzer.services.impl;

import com.github.adam6806.pnlanalyzer.entities.Company;
import com.github.adam6806.pnlanalyzer.repositories.CompanyRepository;
import com.github.adam6806.pnlanalyzer.services.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;

@Service("companyService")
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    @Autowired
    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public List<Company> findAllCompanies() {
        return companyRepository.findAll();
    }

    @Override
    public Company findCompanyById(Long companyId) {
        if (companyRepository.findById(companyId).isPresent()) {
            return companyRepository.getOne(companyId);
        } else {
            throw new EntityNotFoundException("No Company was found for Id: " + companyId);
        }
    }

    @Override
    public void deleteCompanyById(Long companyId) {
        companyRepository.deleteById(companyId);
    }

    @Override
    public void saveCompany(Company company) {
        companyRepository.save(company);
    }
}
