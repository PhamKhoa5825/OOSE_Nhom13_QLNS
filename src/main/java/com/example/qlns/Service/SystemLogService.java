package com.example.qlns.Service;

import com.example.qlns.Entity.SystemLog;
import com.example.qlns.Entity.User;

import java.util.List;

public interface SystemLogService {
    void log(User user, String action, String description);
    void log(String action, String description);
    List<SystemLog> getRecentLogs(int limit);
}