package com.github.adam6806.pnlanalyzer.repositories;

import com.github.adam6806.pnlanalyzer.domain.LineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LineItemRepository extends JpaRepository<LineItem, Long> {
}
