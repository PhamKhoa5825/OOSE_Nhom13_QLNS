package com.example.qlns.Service.Impl;

import com.example.qlns.DTO.Response.AttendanceDTO;
import com.example.qlns.DTO.Response.AttendanceStatsDTO;
import com.example.qlns.DTO.Response.EmployeeAttendanceStatsDTO;
import com.example.qlns.Entity.Attendance;
import com.example.qlns.Entity.CompanySettings;
import com.example.qlns.Entity.Employee;
import com.example.qlns.Enum.AttendanceStatus;
import com.example.qlns.Enum.EmployeeStatus;
import com.example.qlns.Exception.AttendanceException;
import com.example.qlns.Exception.LocationException;
import com.example.qlns.Exception.ResourceNotFoundException;
import com.example.qlns.Mapper.AttendanceMapper;
import com.example.qlns.Repository.AttendanceRepository;
import com.example.qlns.Repository.CompanySettingsRepository;
import com.example.qlns.Repository.EmployeeRepository;
import com.example.qlns.Service.AttendanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepo;
    private final EmployeeRepository empRepo;
    private final CompanySettingsRepository settingsRepo;
    private final AttendanceMapper attendanceMapper;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepo, 
                                 EmployeeRepository empRepo, 
                                 CompanySettingsRepository settingsRepo,
                                 AttendanceMapper attendanceMapper) {
        this.attendanceRepo = attendanceRepo;
        this.empRepo = empRepo;
        this.settingsRepo = settingsRepo;
        this.attendanceMapper = attendanceMapper;
    }

    private double haversine(double lat1, double lng1, double lat2, double lng2) {
        final int R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private CompanySettings getSettings() {
        return settingsRepo.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException("Chưa cấu hình công ty"));
    }

    @Override
    @Transactional
    public AttendanceDTO checkIn(Long employeeId, Double lat, Double lng) {
        Employee emp = empRepo.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên"));

        LocalDate today = LocalDate.now();
        if (attendanceRepo.existsByEmployeeIdAndDate(employeeId, today))
            throw new AttendanceException("Đã chấm công hôm nay rồi");

        CompanySettings settings = getSettings();
        
        if (lat != null && lng != null) {
            double distance = haversine(lat, lng, settings.getBaseLat(), settings.getBaseLng());
            if (distance > settings.getAllowedRadius())
                throw new LocationException(String.format(
                        "Ngoài phạm vi %dm (bạn cách văn phòng %.0fm)",
                        settings.getAllowedRadius(), distance));
        }

        LocalTime startTime    = LocalTime.parse(settings.getWorkStartTime());
        LocalTime nowTime      = LocalTime.now();
        Integer lateMinutes    = 0;
        AttendanceStatus status = AttendanceStatus.ON_TIME;

        if (nowTime.isAfter(startTime)) {
            lateMinutes = (int) Duration.between(startTime, nowTime).toMinutes();
            status      = AttendanceStatus.LATE;
        }

        Attendance att = new Attendance();
        att.setEmployee(emp);
        att.setDate(today);
        att.setCheckIn(LocalDateTime.now());
        att.setLocationLat(lat);
        att.setLocationLng(lng);
        att.setStatus(status);
        att.setLateMinutes(lateMinutes);

        return attendanceMapper.toDTO(attendanceRepo.save(att));
    }

    @Override
    @Transactional
    public AttendanceDTO checkOut(Long employeeId) {
        Attendance att = attendanceRepo
                .findByEmployeeIdAndDate(employeeId, LocalDate.now())
                .orElseThrow(() -> new AttendanceException("Bạn chưa check-in hôm nay"));

        if (att.getCheckOut() != null)
            throw new AttendanceException("Bạn đã check-out rồi");

        CompanySettings settings = getSettings();
        LocalTime endTime = LocalTime.parse(settings.getWorkEndTime());
        if (LocalTime.now().isBefore(endTime))
            throw new AttendanceException(
                    "Chưa đến giờ check-out. Giờ tan ca: " + settings.getWorkEndTime());

        LocalDateTime checkOut = LocalDateTime.now();
        double workHours = Duration.between(att.getCheckIn(), checkOut).toMinutes() / 60.0;

        att.setCheckOut(checkOut);
        att.setWorkHours((float) workHours);

        return attendanceMapper.toDTO(attendanceRepo.save(att));
    }

    @Override
    public List<Attendance> getByEmployee(Long empId) {
        return attendanceRepo.findByEmployeeId(empId);
    }

    @Override
    public List<Attendance> getByDate(LocalDate date) {
        return attendanceRepo.findByDate(date);
    }
    
    @Override
    public List<Attendance> getByDateAndDepartment(LocalDate date, Long deptId) {
        return attendanceRepo.findByDateAndEmployeeDepartmentId(date, deptId);
    }

    @Override
    public List<Attendance> getByEmployeeAndMonth(Long empId, int month, int year) {
        LocalDate from = LocalDate.of(year, month, 1);
        LocalDate to   = from.withDayOfMonth(from.lengthOfMonth());
        return attendanceRepo.findByEmployeeIdAndDateBetween(empId, from, to);
    }

    @Override
    public AttendanceStatsDTO getMonthlyStats(Long empId, int month, int year) {
        List<Attendance> attendances = getByEmployeeAndMonth(empId, month, year);
        
        double totalHours = attendances.stream()
                .filter(a -> a.getWorkHours() != null)
                .mapToDouble(a -> (double) a.getWorkHours())
                .sum();

        long onTime = attendances.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.ON_TIME)
                .count();

        long late = attendances.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.LATE)
                .count();

        long absent = Math.max(0, 22 - (onTime + late));

        double avgHours = attendances.isEmpty() ? 0 : totalHours / attendances.size();
        
        totalHours = Math.round(totalHours * 100.0) / 100.0;
        avgHours = Math.round(avgHours * 100.0) / 100.0;

        return new AttendanceStatsDTO(totalHours, onTime, late, absent, avgHours);
    }

    @Override
    public List<EmployeeAttendanceStatsDTO> getDepartmentStats(Long deptId, int month, int year) {
        List<Employee> employees = empRepo.findByDepartmentIdAndStatus(deptId, EmployeeStatus.ACTIVE);
        return employees.stream().map(emp -> {
            long onTime = attendanceRepo.countByEmployeeAndStatus(emp.getId(), month, year, AttendanceStatus.ON_TIME);
            long late = attendanceRepo.countByEmployeeAndStatus(emp.getId(), month, year, AttendanceStatus.LATE);
            long absent = Math.max(0, 22 - (onTime + late));
            return new EmployeeAttendanceStatsDTO(emp.getId(), emp.getFullName(), onTime, late, absent);
        }).collect(Collectors.toList());
    }

    @Override
    public long countWorkingDays(Long empId, int month, int year) {
        return attendanceRepo.countWorkingDays(empId, month, year);
    }
}
