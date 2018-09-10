package com.github.adam6806.pnlanalyzer.repositories;

import com.github.adam6806.pnlanalyzer.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Company findCompanyByName(String name);
}
