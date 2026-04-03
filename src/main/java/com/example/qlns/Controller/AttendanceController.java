package com.example.qlns.Controller;

import com.example.qlns.DTO.Request.CheckInRequest;
import com.example.qlns.DTO.Request.CheckOutRequest;
import com.example.qlns.DTO.Response.AttendanceDTO;
import com.example.qlns.DTO.Response.AttendanceStatsDTO;
import com.example.qlns.DTO.Response.EmployeeAttendanceStatsDTO;
import com.example.qlns.Mapper.AttendanceMapper;
import com.example.qlns.Security.SecurityService;
import com.example.qlns.Service.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final SecurityService securityService;
    private final AttendanceMapper attendanceMapper;

    public AttendanceController(AttendanceService attendanceService, 
                                SecurityService securityService, 
                                AttendanceMapper attendanceMapper) {
        this.attendanceService = attendanceService;
        this.securityService = securityService;
        this.attendanceMapper = attendanceMapper;
    }

    @PostMapping("/checkin")
    public ResponseEntity<AttendanceDTO> checkIn(@RequestBody CheckInRequest req) {
        return ResponseEntity.ok(attendanceService.checkIn(
                req.getEmployeeId(), req.getLatitude(), req.getLongitude()));
    }

    @PutMapping("/checkout")
    public ResponseEntity<AttendanceDTO> checkOut(@RequestBody CheckOutRequest req) {
        return ResponseEntity.ok(attendanceService.checkOut(req.getEmployeeId()));
    }

    @GetMapping("/employee/{empId}")
    public ResponseEntity<List<AttendanceDTO>> getByEmployee(@PathVariable("empId") Long empId) {
        return ResponseEntity.ok(
                attendanceService.getByEmployee(empId)
                        .stream()
                        .map(attendanceMapper::toDTO)
                        .collect(Collectors.toList()));
    }

    @GetMapping("/employee/{empId}/month")
    public ResponseEntity<List<AttendanceDTO>> getByMonth(
            @PathVariable("empId") Long empId,
            @RequestParam("month") int month,
            @RequestParam("year") int year) {
        return ResponseEntity.ok(
                attendanceService.getByEmployeeAndMonth(empId, month, year)
                        .stream()
                        .map(attendanceMapper::toDTO)
                        .collect(Collectors.toList()));
    }

    @GetMapping("/employee/{empId}/working-days")
    public ResponseEntity<Long> countWorkingDays(
            @PathVariable("empId") Long empId,
            @RequestParam("month") int month,
            @RequestParam("year") int year) {
        return ResponseEntity.ok(attendanceService.countWorkingDays(empId, month, year));
    }

    @GetMapping("/today")
    public ResponseEntity<List<AttendanceDTO>> getToday() {
        return ResponseEntity.ok(
                attendanceService.getByDate(LocalDate.now())
                        .stream()
                        .map(attendanceMapper::toDTO)
                        .collect(Collectors.toList()));
    }

    @GetMapping("/today/department/{deptId}")
    public ResponseEntity<List<AttendanceDTO>> getTodayByDepartment(@PathVariable("deptId") Long deptId) {
        securityService.validateManagerDepartment(deptId);
        return ResponseEntity.ok(
                attendanceService.getByDateAndDepartment(LocalDate.now(), deptId)
                        .stream()
                        .map(attendanceMapper::toDTO)
                        .collect(Collectors.toList()));
    }

    @GetMapping("/department/{deptId}/date")
    public ResponseEntity<List<AttendanceDTO>> getByDepartmentAndDate(
            @PathVariable("deptId") Long deptId,
            @RequestParam("date") String date) {
        securityService.validateManagerDepartment(deptId);
        LocalDate targetDate = LocalDate.parse(date);
        return ResponseEntity.ok(
                attendanceService.getByDateAndDepartment(targetDate, deptId)
                        .stream()
                        .map(attendanceMapper::toDTO)
                        .collect(Collectors.toList()));
    }

    @GetMapping("/employee/{empId}/stats")
    public ResponseEntity<AttendanceStatsDTO> getStats(
            @PathVariable("empId") Long empId,
            @RequestParam("month") int month,
            @RequestParam("year") int year) {
        return ResponseEntity.ok(attendanceService.getMonthlyStats(empId, month, year));
    }

    @GetMapping("/employee/{empId}/statistics")
    public ResponseEntity<AttendanceStatsDTO> getEmployeeStats(
            @PathVariable("empId") Long empId,
            @RequestParam("month") int month,
            @RequestParam("year") int year) {
        return ResponseEntity.ok(attendanceService.getMonthlyStats(empId, month, year));
    }

    @GetMapping("/department/{deptId}/statistics")
    public ResponseEntity<List<EmployeeAttendanceStatsDTO>> getDepartmentStats(
            @PathVariable("deptId") Long deptId,
            @RequestParam("month") int month,
            @RequestParam("year") int year) {
        securityService.validateManagerDepartment(deptId);
        return ResponseEntity.ok(attendanceService.getDepartmentStats(deptId, month, year));
    }
}