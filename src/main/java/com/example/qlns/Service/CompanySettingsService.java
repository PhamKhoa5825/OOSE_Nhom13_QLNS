package com.example.qlns.Service;

import com.example.qlns.Entity.CompanySettings;

public interface CompanySettingsService {
    CompanySettings get();
    CompanySettings update(CompanySettings req);
}