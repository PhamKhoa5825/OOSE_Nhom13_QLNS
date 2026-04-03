package com.example.qlns.DTO.Response;

import java.util.List;
import java.util.Map;

/**
 * Composite DTO for the main dashboard view.
 */
public class DashboardDataDTO {
    private long totalEmployees;
    private double onTimeRate;
    private long activeTasksCount;
    private long overdueTasksCount;

    private List<EmployeeDTO> recentEmployees;
    private Map<String, Long> taskStatusCounts; // "PENDING", "ACCEPTED", "DONE", "OVERDUE"
    private List<TaskDTO> kanbanTasks; // Recently updated or categorized
    private List<AttendanceDTO> todayAttendance;
    private List<RequestDTO> pendingRequests;
    private List<NotificationDTO> notifications;

    // Attendance Chart Data (Last 7 days or 5 days)
    private List<Double> weeklyAttendanceRates;
    private List<String> weeklyAttendanceDays;

    public DashboardDataDTO() {}

    // Getters & Setters
    public long getTotalEmployees() { return totalEmployees; }
    public void setTotalEmployees(long totalEmployees) { this.totalEmployees = totalEmployees; }

    public double getOnTimeRate() { return onTimeRate; }
    public void setOnTimeRate(double onTimeRate) { this.onTimeRate = onTimeRate; }

    public long getActiveTasksCount() { return activeTasksCount; }
    public void setActiveTasksCount(long activeTasksCount) { this.activeTasksCount = activeTasksCount; }

    public long getOverdueTasksCount() { return overdueTasksCount; }
    public void setOverdueTasksCount(long overdueTasksCount) { this.overdueTasksCount = overdueTasksCount; }

    public List<EmployeeDTO> getRecentEmployees() { return recentEmployees; }
    public void setRecentEmployees(List<EmployeeDTO> recentEmployees) { this.recentEmployees = recentEmployees; }

    public Map<String, Long> getTaskStatusCounts() { return taskStatusCounts; }
    public void setTaskStatusCounts(Map<String, Long> taskStatusCounts) { this.taskStatusCounts = taskStatusCounts; }

    public List<TaskDTO> getKanbanTasks() { return kanbanTasks; }
    public void setKanbanTasks(List<TaskDTO> kanbanTasks) { this.kanbanTasks = kanbanTasks; }

    public List<AttendanceDTO> getTodayAttendance() { return todayAttendance; }
    public void setTodayAttendance(List<AttendanceDTO> todayAttendance) { this.todayAttendance = todayAttendance; }

    public List<RequestDTO> getPendingRequests() { return pendingRequests; }
    public void setPendingRequests(List<RequestDTO> pendingRequests) { this.pendingRequests = pendingRequests; }

    public List<NotificationDTO> getNotifications() { return notifications; }
    public void setNotifications(List<NotificationDTO> notifications) { this.notifications = notifications; }

    public List<Double> getWeeklyAttendanceRates() { return weeklyAttendanceRates; }
    public void setWeeklyAttendanceRates(List<Double> weeklyAttendanceRates) { this.weeklyAttendanceRates = weeklyAttendanceRates; }

    public List<String> getWeeklyAttendanceDays() { return weeklyAttendanceDays; }
    public void setWeeklyAttendanceDays(List<String> weeklyAttendanceDays) { this.weeklyAttendanceDays = weeklyAttendanceDays; }
}
