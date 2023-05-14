package com.safebox.integration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.safebox.integration.entity.SafeBoxEntity;

@Repository
public interface SafeBoxRepository extends JpaRepository<SafeBoxEntity, Long> {
}
