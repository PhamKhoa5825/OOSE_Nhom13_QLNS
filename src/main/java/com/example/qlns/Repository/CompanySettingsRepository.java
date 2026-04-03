package com.example.qlns.Repository;

import com.example.qlns.Entity.CompanySettings;
import org.springframework.data.jpa.repository.JpaRepository;

// =============================================
// TV1 - CompanySettingsRepository
// =============================================
public interface CompanySettingsRepository extends JpaRepository<CompanySettings, Long> {
    // Chỉ có 1 bản ghi settings, luôn lấy id=1
    // Dùng findById(1L) hoặc findAll().get(0)
}