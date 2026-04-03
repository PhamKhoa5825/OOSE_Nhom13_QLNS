package com.example.qlns.Service.Impl;

import com.example.qlns.Entity.CompanySettings;
import com.example.qlns.Repository.CompanySettingsRepository;
import com.example.qlns.Service.CompanySettingsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompanySettingsServiceImpl implements CompanySettingsService {

    private final CompanySettingsRepository repo;

    public CompanySettingsServiceImpl(CompanySettingsRepository repo) {
        this.repo = repo;
    }

    @Override
    public CompanySettings get() {
        return repo.findById(1L).orElseGet(() -> {
            CompanySettings defaults = new CompanySettings();
            defaults.setCompanyName("Công ty QLNS");
            return repo.save(defaults);
        });
    }

    @Override
    @Transactional
    public CompanySettings update(CompanySettings req) {
        CompanySettings settings = get();
        if (req.getCompanyName() != null) settings.setCompanyName(req.getCompanyName());
        if (req.getBaseLat() != null) settings.setBaseLat(req.getBaseLat());
        if (req.getBaseLng() != null) settings.setBaseLng(req.getBaseLng());
        if (req.getAllowedRadius() != null) settings.setAllowedRadius(req.getAllowedRadius());
        if (req.getWorkStartTime() != null) settings.setWorkStartTime(req.getWorkStartTime());
        if (req.getWorkEndTime() != null) settings.setWorkEndTime(req.getWorkEndTime());
        return repo.save(settings);
    }
}