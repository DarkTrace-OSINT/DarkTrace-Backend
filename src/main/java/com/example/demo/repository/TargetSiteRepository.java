package com.example.demo.repository;

import com.example.demo.entity.TargetSite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TargetSiteRepository extends JpaRepository<TargetSite, Long> {
    long countByCrawlerStatus(String status);
}