package com.example.qlns.Controller;

import com.example.qlns.Entity.CompanySettings;
import com.example.qlns.Service.CompanySettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
public class CompanySettingsController {

    private final CompanySettingsService service;

    public CompanySettingsController(CompanySettingsService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<CompanySettings> get() {
        return ResponseEntity.ok(service.get());
    }

    @PutMapping
    public ResponseEntity<CompanySettings> update(@RequestBody CompanySettings req) {
        return ResponseEntity.ok(service.update(req));
    }
}