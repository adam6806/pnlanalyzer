package com.github.adam6806.pnlanalyzer.pnl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PnlRepository extends JpaRepository<Pnl, Long> {

}
