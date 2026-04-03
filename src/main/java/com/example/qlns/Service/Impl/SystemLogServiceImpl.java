package com.example.qlns.Service.Impl;

import com.example.qlns.Entity.SystemLog;
import com.example.qlns.Entity.User;
import com.example.qlns.Repository.SystemLogRepository;
import com.example.qlns.Service.SystemLogService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemLogServiceImpl implements SystemLogService {

    private final SystemLogRepository logRepo;

    public SystemLogServiceImpl(SystemLogRepository logRepo) {
        this.logRepo = logRepo;
    }

    @Override
    public void log(User user, String action, String description) {
        logRepo.save(new SystemLog(user, action, description));
    }

    @Override
    public void log(String action, String description) {
        logRepo.save(new SystemLog(null, action, description));
    }

    @Override
    public List<SystemLog> getRecentLogs(int limit) {
        return logRepo.findTop50ByOrderByCreatedAtDesc();
    }
}