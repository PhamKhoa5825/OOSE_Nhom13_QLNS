-- =============================================
-- QLNS - SEED DATA v4 (SẠCH - không xung đột)
-- Mật khẩu tất cả: password123
-- BCrypt: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- =============================================
USE qlns;

SET FOREIGN_KEY_CHECKS = 0;

-- ── Xoá tất cả data cũ ───────────────────────────────────────
TRUNCATE TABLE system_logs;
TRUNCATE TABLE user_notifications;
TRUNCATE TABLE notifications;
TRUNCATE TABLE messages;
TRUNCATE TABLE chat_room_members;
TRUNCATE TABLE chat_rooms;
TRUNCATE TABLE requests;
TRUNCATE TABLE task_updates;
TRUNCATE TABLE tasks;
TRUNCATE TABLE attendance;
TRUNCATE TABLE company_settings;
TRUNCATE TABLE employees;
TRUNCATE TABLE users;
TRUNCATE TABLE departments;

SET FOREIGN_KEY_CHECKS = 1;

-- ── Đảm bảo enum tasks.status đúng v4 ────────────────────────
ALTER TABLE tasks
    MODIFY COLUMN status ENUM('PENDING','ACCEPTED','DONE','OVERDUE') NOT NULL DEFAULT 'PENDING';

ALTER TABLE task_updates
    MODIFY COLUMN status ENUM('PENDING','ACCEPTED','DONE','OVERDUE') NOT NULL;

-- ── Đảm bảo cột late_minutes tồn tại ─────────────────────────
-- ALTER TABLE attendance
--     ADD COLUMN IF NOT EXISTS late_minutes INT NOT NULL DEFAULT 0;

-- =============================================
-- 1. COMPANY SETTINGS
-- =============================================
INSERT INTO company_settings (id, company_name, base_lat, base_lng, allowed_radius, work_start_time, work_end_time)
VALUES (1, 'Công ty TNHH QLNS', 10.7769, 106.7009, 1000, '08:00', '17:30');

-- =============================================
-- 2. DEPARTMENTS
-- =============================================
INSERT INTO departments (id, name, description, manager_id, created_at) VALUES
                                                                            (1, 'Công Nghệ Thông Tin', 'Phụ trách phát triển và vận hành hệ thống', NULL, NOW()),
                                                                            (2, 'Nhân Sự',             'Quản lý tuyển dụng và nhân sự',              NULL, NOW()),
                                                                            (3, 'Kế Toán',             'Quản lý tài chính và kế toán',               NULL, NOW()),
                                                                            (4, 'Kinh Doanh',          'Phát triển kinh doanh và bán hàng',          NULL, NOW()),
                                                                            (5, 'Hành Chính',          'Quản lý hành chính và hậu cần',              NULL, NOW());

-- =============================================
-- 3. EMPLOYEES
-- =============================================
INSERT INTO employees (id, full_name, email, phone, address, date_of_birth, gender, avatar_url, position, join_date, status, department_id, created_at) VALUES
                                                                                                                                                            (1,  'Nguyễn Văn Admin',   'admin@company.com',       '0901000001', '123 Lê Lợi, Q1, TP.HCM',        '1985-03-15', 'MALE',   NULL, 'Quản trị hệ thống',    '2020-01-01', 'ACTIVE',   1, NOW()),
                                                                                                                                                            (2,  'Trần Thị Manager',   'manager.it@company.com',  '0901000002', '456 Nguyễn Huệ, Q1, TP.HCM',    '1988-07-20', 'FEMALE', NULL, 'Trưởng phòng CNTT',    '2020-03-01', 'ACTIVE',   1, NOW()),
                                                                                                                                                            (3,  'Lê Văn Lập Trình',   'lap.lv@company.com',      '0901000003', '789 Hai Bà Trưng, Q3, TP.HCM',  '1995-11-10', 'MALE',   NULL, 'Lập trình viên',       '2021-06-01', 'ACTIVE',   1, NOW()),
                                                                                                                                                            (4,  'Phạm Thị Tester',    'tester.pt@company.com',   '0901000004', '321 Điện Biên Phủ, BT, TP.HCM', '1996-04-25', 'FEMALE', NULL, 'Kiểm thử phần mềm',   '2022-01-10', 'ACTIVE',   1, NOW()),
                                                                                                                                                            (5,  'Võ Thị Manager HR',  'manager.hr@company.com',  '0901000005', '654 Nam Kỳ Khởi Nghĩa, Q3',     '1990-09-05', 'FEMALE', NULL, 'Trưởng phòng Nhân Sự', '2020-06-01', 'ACTIVE',   2, NOW()),
                                                                                                                                                            (6,  'Hoàng Văn Nhân Sự',  'nhansu.hv@company.com',   '0901000006', '987 Cộng Hòa, Tân Bình',        '1997-02-14', 'MALE',   NULL, 'Chuyên viên Nhân Sự',  '2022-08-01', 'ACTIVE',   2, NOW()),
                                                                                                                                                            (7,  'Đặng Thị Kế Toán',   'ketoan.dt@company.com',   '0901000007', '147 Trường Chinh, Tân Bình',    '1993-06-30', 'FEMALE', NULL, 'Kế toán trưởng',       '2021-01-15', 'ACTIVE',   3, NOW()),
                                                                                                                                                            (8,  'Bùi Văn Kinh Doanh', 'kinhdoanh.bv@company.com','0901000008', '258 Lý Thường Kiệt, Q10',       '1994-12-08', 'MALE',   NULL, 'Nhân viên Kinh Doanh', '2022-03-01', 'ACTIVE',   4, NOW()),
                                                                                                                                                            (9,  'Ngô Thị Hành Chính', 'hanhchinh.nt@company.com','0901000009', '369 Võ Văn Tần, Q3',            '1991-08-17', 'FEMALE', NULL, 'Nhân viên Hành Chính', '2021-09-01', 'ACTIVE',   5, NOW()),
                                                                                                                                                            (10, 'Trịnh Văn Cũ',       'cu.tv@company.com',        '0901000010', '101 Pasteur, Q1',               '1989-01-22', 'MALE',   NULL, 'Lập trình viên',       '2020-05-01', 'RESIGNED', 1, NOW());

-- =============================================
-- 4. USERS
-- =============================================
INSERT INTO users (id, username, email, password_hash, role, status, employee_id, created_at) VALUES
                                                                                                  (1,  'admin',        'admin@company.com',        '$2a$10$cRkrRUyZv6yHQp00hFkqTuZF7WtAt5AoLM6iVOXY2Z6W7xr5PzRka', 'ADMIN',    'ACTIVE',   1,  NOW()),
                                                                                                  (2,  'manager_it',   'manager.it@company.com',   '$2a$10$cRkrRUyZv6yHQp00hFkqTuZF7WtAt5AoLM6iVOXY2Z6W7xr5PzRka', 'MANAGER',  'ACTIVE',   2,  NOW()),
                                                                                                  (3,  'lap_lv',       'lap.lv@company.com',        '$2a$10$cRkrRUyZv6yHQp00hFkqTuZF7WtAt5AoLM6iVOXY2Z6W7xr5PzRka', 'EMPLOYEE', 'ACTIVE',   3,  NOW()),
                                                                                                  (4,  'tester_pt',    'tester.pt@company.com',     '$2a$10$cRkrRUyZv6yHQp00hFkqTuZF7WtAt5AoLM6iVOXY2Z6W7xr5PzRka', 'EMPLOYEE', 'ACTIVE',   4,  NOW()),
                                                                                                  (5,  'manager_hr',   'manager.hr@company.com',    '$2a$10$cRkrRUyZv6yHQp00hFkqTuZF7WtAt5AoLM6iVOXY2Z6W7xr5PzRka', 'MANAGER',  'ACTIVE',   5,  NOW()),
                                                                                                  (6,  'nhansu_hv',    'nhansu.hv@company.com',     '$2a$10$cRkrRUyZv6yHQp00hFkqTuZF7WtAt5AoLM6iVOXY2Z6W7xr5PzRka', 'EMPLOYEE', 'ACTIVE',   6,  NOW()),
                                                                                                  (7,  'ketoan_dt',    'ketoan.dt@company.com',      '$2a$10$cRkrRUyZv6yHQp00hFkqTuZF7WtAt5AoLM6iVOXY2Z6W7xr5PzRka', 'EMPLOYEE', 'ACTIVE',   7,  NOW()),
                                                                                                  (8,  'kinhdoanh_bv', 'kinhdoanh.bv@company.com',  '$2a$10$cRkrRUyZv6yHQp00hFkqTuZF7WtAt5AoLM6iVOXY2Z6W7xr5PzRka', 'EMPLOYEE', 'ACTIVE',   8,  NOW()),
                                                                                                  (9,  'hanhchinh_nt', 'hanhchinh.nt@company.com',  '$2a$10$cRkrRUyZv6yHQp00hFkqTuZF7WtAt5AoLM6iVOXY2Z6W7xr5PzRka', 'EMPLOYEE', 'ACTIVE',   9,  NOW()),
                                                                                                  (10, 'cu_tv',        'cu.tv@company.com',           '$2a$10$cRkrRUyZv6yHQp00hFkqTuZF7WtAt5AoLM6iVOXY2Z6W7xr5PzRka', 'EMPLOYEE', 'INACTIVE', 10, NOW());

-- =============================================
-- 5. SET MANAGER CHO DEPARTMENTS
-- =============================================
UPDATE departments SET manager_id = 2 WHERE id = 1;
UPDATE departments SET manager_id = 5 WHERE id = 2;
UPDATE departments SET manager_id = 7 WHERE id = 3;
UPDATE departments SET manager_id = 8 WHERE id = 4;
UPDATE departments SET manager_id = 9 WHERE id = 5;

-- =============================================
-- 6. ATTENDANCE (có late_minutes)
-- =============================================
INSERT INTO attendance (employee_id, date, check_in, check_out, work_hours, location_lat, location_lng, status, late_minutes, created_at) VALUES
-- Hôm nay
(3, CURDATE(), CONCAT(CURDATE(), ' 07:55:00'), CONCAT(CURDATE(), ' 17:30:00'), 9.5,  10.7765, 106.7005, 'ON_TIME', 0,  NOW()),
(4, CURDATE(), CONCAT(CURDATE(), ' 08:35:00'), CONCAT(CURDATE(), ' 17:30:00'), 8.9,  10.7770, 106.7010, 'LATE',    35, NOW()),
(2, CURDATE(), CONCAT(CURDATE(), ' 07:50:00'), NULL,                            NULL, 10.7769, 106.7009, 'ON_TIME', 0,  NOW()),
-- Hôm qua
(3, CURDATE()-INTERVAL 1 DAY, CONCAT(CURDATE()-INTERVAL 1 DAY,' 08:00:00'), CONCAT(CURDATE()-INTERVAL 1 DAY,' 17:30:00'), 9.5, 10.7765, 106.7005, 'ON_TIME', 0,  NOW()),
(4, CURDATE()-INTERVAL 1 DAY, CONCAT(CURDATE()-INTERVAL 1 DAY,' 07:58:00'), CONCAT(CURDATE()-INTERVAL 1 DAY,' 17:30:00'), 9.5, 10.7770, 106.7010, 'ON_TIME', 0,  NOW()),
(6, CURDATE()-INTERVAL 1 DAY, CONCAT(CURDATE()-INTERVAL 1 DAY,' 09:10:00'), CONCAT(CURDATE()-INTERVAL 1 DAY,' 17:30:00'), 8.3, 10.7769, 106.7009, 'LATE',    70, NOW()),
-- 2 ngày trước
(3, CURDATE()-INTERVAL 2 DAY, CONCAT(CURDATE()-INTERVAL 2 DAY,' 07:55:00'), CONCAT(CURDATE()-INTERVAL 2 DAY,' 17:30:00'), 9.5, 10.7765, 106.7005, 'ON_TIME', 0,  NOW()),
(2, CURDATE()-INTERVAL 2 DAY, CONCAT(CURDATE()-INTERVAL 2 DAY,' 08:00:00'), CONCAT(CURDATE()-INTERVAL 2 DAY,' 17:30:00'), 9.5, 10.7769, 106.7009, 'ON_TIME', 0,  NOW()),
-- 3 ngày trước
(3, CURDATE()-INTERVAL 3 DAY, CONCAT(CURDATE()-INTERVAL 3 DAY,' 08:05:00'), CONCAT(CURDATE()-INTERVAL 3 DAY,' 17:30:00'), 9.4, 10.7765, 106.7005, 'ON_TIME', 5,  NOW()),
(4, CURDATE()-INTERVAL 3 DAY, CONCAT(CURDATE()-INTERVAL 3 DAY,' 08:45:00'), CONCAT(CURDATE()-INTERVAL 3 DAY,' 17:30:00'), 8.8, 10.7770, 106.7010, 'LATE',    45, NOW()),
-- 4 ngày trước
(3, CURDATE()-INTERVAL 4 DAY, CONCAT(CURDATE()-INTERVAL 4 DAY,' 07:50:00'), CONCAT(CURDATE()-INTERVAL 4 DAY,' 17:30:00'), 9.7, 10.7765, 106.7005, 'ON_TIME', 0,  NOW()),
(6, CURDATE()-INTERVAL 4 DAY, CONCAT(CURDATE()-INTERVAL 4 DAY,' 08:00:00'), CONCAT(CURDATE()-INTERVAL 4 DAY,' 17:30:00'), 9.5, 10.7769, 106.7009, 'ON_TIME', 0,  NOW());

-- =============================================
-- 7. TASKS  ← v4: dùng PENDING/ACCEPTED/DONE/OVERDUE
-- =============================================
INSERT INTO tasks (id, title, description, assigned_by, assigned_to, priority, status, deadline, completed_at, attachment_url, created_at) VALUES
                                                                                                                                               (1, 'Thiết kế database v4',        'Cập nhật schema theo tài liệu mới',           2, 3,  'HIGH',   'DONE',     NOW()-INTERVAL 5 DAY,  NOW()-INTERVAL 1 DAY, NULL, NOW()-INTERVAL 7 DAY),
                                                                                                                                               (2, 'Viết API chấm công',          'Implement AttendanceController và Service',    2, 3,  'HIGH',   'ACCEPTED', NOW()+INTERVAL 3 DAY,  NULL,                 NULL, NOW()-INTERVAL 2 DAY),
                                                                                                                                               (3, 'Viết API chat nội bộ',        'Implement ChatController và polling',          2, 4,  'MEDIUM', 'PENDING',  NOW()+INTERVAL 7 DAY,  NULL,                 NULL, NOW()-INTERVAL 1 DAY),
                                                                                                                                               (4, 'Kiểm thử module đăng nhập',   'Test JWT auth và phân quyền',                 2, 4,  'HIGH',   'ACCEPTED', NOW()+INTERVAL 2 DAY,  NULL,                 NULL, NOW()-INTERVAL 3 DAY),
                                                                                                                                               (5, 'Viết báo cáo tháng',          'Tổng hợp báo cáo nhân sự tháng này',          5, 6,  'MEDIUM', 'PENDING',  NOW()+INTERVAL 10 DAY, NULL,                 NULL, NOW()),
                                                                                                                                               (6, 'Cập nhật hợp đồng nhân viên', 'Rà soát và cập nhật hợp đồng hết hạn',       5, 6,  'LOW',    'DONE',     NOW()-INTERVAL 2 DAY,  NOW()-INTERVAL 1 DAY, NULL, NOW()-INTERVAL 5 DAY),
                                                                                                                                               (7, 'Đối soát công nợ Q3',         'Kiểm tra và đối soát công nợ quý 3',          1, 7,  'URGENT', 'ACCEPTED', NOW()+INTERVAL 1 DAY,  NULL,                 NULL, NOW()-INTERVAL 1 DAY),
                                                                                                                                               (8, 'Lập kế hoạch kinh doanh Q4',  'Xây dựng kế hoạch và mục tiêu Q4',           1, 8,  'HIGH',   'PENDING',  NOW()+INTERVAL 14 DAY, NULL,                 NULL, NOW()),
-- Task quá hạn (để test scheduler OVERDUE)
                                                                                                                                               (9, 'Báo cáo Q2 chưa nộp',         'Deadline đã qua, chưa hoàn thành',            1, 7,  'HIGH',   'OVERDUE',  NOW()-INTERVAL 3 DAY,  NULL,                 NULL, NOW()-INTERVAL 10 DAY);

-- =============================================
-- 8. TASK UPDATES  ← v4: dùng status mới
-- =============================================
INSERT INTO task_updates (task_id, status, note, updated_by, updated_at) VALUES
                                                                             (1, 'ACCEPTED', 'Nhân viên đã nhận việc',              3, NOW()-INTERVAL 6 DAY),
                                                                             (1, 'DONE',     'Hoàn thành, đã review với team lead', 3, NOW()-INTERVAL 1 DAY),
                                                                             (2, 'ACCEPTED', 'Đang viết AttendanceService',          3, NOW()-INTERVAL 1 DAY),
                                                                             (4, 'ACCEPTED', 'Đang viết test case cho JWT',          4, NOW()-INTERVAL 2 DAY),
                                                                             (6, 'ACCEPTED', 'Đã rà soát 10/15 hợp đồng',          6, NOW()-INTERVAL 3 DAY),
                                                                             (6, 'DONE',     'Đã hoàn tất cập nhật toàn bộ',        6, NOW()-INTERVAL 1 DAY),
                                                                             (9, 'OVERDUE',  'Tự động đánh dấu quá hạn bởi hệ thống', 1, NOW()-INTERVAL 3 DAY);

-- =============================================
-- 9. REQUESTS  ← v4: thay thế leave_requests + leave_balance
--    Chỉ INSERT 1 LẦN
-- =============================================
INSERT INTO requests (employee_id, title, description, file_url, file_name, status, reviewed_by, rejection_reason, created_at) VALUES
                                                                                                                                   (3, 'Xin nghỉ phép 2 ngày',
                                                                                                                                    'Nghỉ phép cá nhân ngày 20-21/3/2026',
                                                                                                                                    NULL, NULL, 'PENDING', NULL, NULL, NOW()-INTERVAL 1 DAY),

                                                                                                                                   (6, 'Đơn xin tăng ca cuối tuần',
                                                                                                                                    'Yêu cầu làm thêm thứ 7 tuần này để hoàn thiện báo cáo',
                                                                                                                                    NULL, NULL, 'APPROVED', 5, NULL, NOW()-INTERVAL 3 DAY),

                                                                                                                                   (4, 'Xin nghỉ ốm',
                                                                                                                                    'Bị sốt, có giấy bác sĩ đính kèm',
                                                                                                                                    'https://storage.example.com/files/giay_bac_si.pdf',
                                                                                                                                    'giay_bac_si.pdf', 'APPROVED', 2, NULL, NOW()-INTERVAL 5 DAY),

                                                                                                                                   (7, 'Xin chỉnh lại chấm công',
                                                                                                                                    'Hôm qua quên check-out do ra ngoài công tác',
                                                                                                                                    NULL, NULL, 'REJECTED', 1,
                                                                                                                                    'Cần xác nhận lại với trưởng phòng trực tiếp', NOW()-INTERVAL 2 DAY),

                                                                                                                                   (8, 'Đăng ký đào tạo tháng 4',
                                                                                                                                    'Xin tham gia khóa học Sales Pro tháng 4',
                                                                                                                                    'https://storage.example.com/files/khoa_hoc_sales.pdf',
                                                                                                                                    'khoa_hoc_sales.pdf', 'PENDING', NULL, NULL, NOW()),

                                                                                                                                   (3, 'Đơn xin đổi ca làm việc',
                                                                                                                                    'Xin đổi ca chiều thứ 4 tuần tới vì có việc gia đình',
                                                                                                                                    NULL, NULL, 'PENDING', NULL, NULL, NOW()-INTERVAL 4 HOUR),

                                                                                                                                   (9, 'Xin phép về sớm',
                                                                                                                                    'Cần về lúc 15h ngày mai để đón con',
                                                                                                                                    NULL, NULL, 'APPROVED', 5, NULL, NOW()-INTERVAL 6 DAY);

-- =============================================
-- 10. CHAT ROOMS
-- =============================================
INSERT INTO chat_rooms (id, name, type, department_id, created_at) VALUES
                                                                       (1, 'Chat chung CNTT',    'DEPARTMENT', 1,    NOW()),
                                                                       (2, 'Chat chung Nhân Sự', 'DEPARTMENT', 2,    NOW()),
                                                                       (3, NULL,                 'PRIVATE',    NULL, NOW()),
                                                                       (4, NULL,                 'PRIVATE',    NULL, NOW());

-- =============================================
-- 11. CHAT ROOM MEMBERS
-- =============================================
INSERT INTO chat_room_members (room_id, user_id, joined_at) VALUES
                                                                (1,1,NOW()),(1,2,NOW()),(1,3,NOW()),(1,4,NOW()),
                                                                (2,5,NOW()),(2,6,NOW()),
                                                                (3,2,NOW()),(3,3,NOW()),
                                                                (4,1,NOW()),(4,5,NOW());

-- =============================================
-- 12. MESSAGES
-- =============================================
INSERT INTO messages (room_id, sender_id, message, message_type, created_at) VALUES
                                                                                 (1, 2, 'Anh em ơi, deadline API chấm công là thứ 6 nhé!', 'TEXT', NOW()-INTERVAL 2 HOUR),
                                                                                 (1, 3, 'Vâng anh, em đang làm rồi ạ',                      'TEXT', NOW()-INTERVAL 1 HOUR),
                                                                                 (1, 4, 'Em cũng đang test module login',                    'TEXT', NOW()-INTERVAL 30 MINUTE),
                                                                                 (1, 2, 'Tốt, nếu có khó khăn thì tag anh',                 'TEXT', NOW()-INTERVAL 20 MINUTE),
                                                                                 (2, 5, 'Nhắc anh Hoàng nộp báo cáo trước 5h chiều nay',    'TEXT', NOW()-INTERVAL 3 HOUR),
                                                                                 (2, 6, 'Vâng chị, em sẽ nộp đúng giờ',                     'TEXT', NOW()-INTERVAL 2 HOUR),
                                                                                 (3, 2, 'Lập, API attendance xong chưa?',                    'TEXT', NOW()-INTERVAL 4 HOUR),
                                                                                 (3, 3, 'Dạ còn checkout endpoint nữa anh',                  'TEXT', NOW()-INTERVAL 3 HOUR),
                                                                                 (4, 1, 'Chị ơi cho anh danh sách nhân viên mới tháng này', 'TEXT', NOW()-INTERVAL 5 HOUR),
                                                                                 (4, 5, 'Để em gửi file ngay ạ',                             'TEXT', NOW()-INTERVAL 4 HOUR);

-- =============================================
-- 13. NOTIFICATIONS
-- =============================================
INSERT INTO notifications (id, title, content, target_type, department_id, created_by, created_at) VALUES
                                                                                                       (1, 'Thông báo nghỉ lễ 30/4',
                                                                                                        'Công ty thông báo nghỉ lễ từ ngày 30/4 đến hết ngày 2/5.',
                                                                                                        'COMPANY', NULL, 1, NOW()-INTERVAL 3 DAY),
                                                                                                       (2, 'Họp phòng CNTT định kỳ',
                                                                                                        'Họp phòng vào thứ 6 tuần này, 15h00, phòng họp A.',
                                                                                                        'DEPARTMENT', 1, 2, NOW()-INTERVAL 1 DAY),
                                                                                                       (3, 'Nộp bảng chấm công tháng',
                                                                                                        'Toàn bộ nhân viên xác nhận bảng chấm công trước ngày 25.',
                                                                                                        'COMPANY', NULL, 1, NOW()-INTERVAL 2 DAY),
                                                                                                       (4, 'Cập nhật hồ sơ cá nhân',
                                                                                                        'Phòng Nhân Sự yêu cầu nhân viên cập nhật thông tin cá nhân.',
                                                                                                        'DEPARTMENT', 2, 5, NOW());

-- =============================================
-- 14. USER NOTIFICATIONS
-- =============================================
INSERT INTO user_notifications (user_id, notification_id, is_read, read_at) VALUES
-- Thông báo 1 (COMPANY)
(1,1,1,NOW()-INTERVAL 2 DAY),(2,1,1,NOW()-INTERVAL 2 DAY),
(3,1,1,NOW()-INTERVAL 1 DAY),(4,1,0,NULL),(5,1,1,NOW()-INTERVAL 2 DAY),
(6,1,0,NULL),(7,1,1,NOW()-INTERVAL 1 DAY),(8,1,0,NULL),(9,1,1,NOW()-INTERVAL 2 DAY),
-- Thông báo 2 (CNTT)
(1,2,1,NOW()-INTERVAL 1 DAY),(2,2,1,NOW()-INTERVAL 1 DAY),(3,2,0,NULL),(4,2,0,NULL),
-- Thông báo 3 (COMPANY)
(1,3,1,NOW()-INTERVAL 1 DAY),(2,3,1,NOW()-INTERVAL 1 DAY),(3,3,0,NULL),(4,3,0,NULL),
(5,3,1,NOW()-INTERVAL 1 DAY),(6,3,0,NULL),(7,3,0,NULL),(8,3,0,NULL),(9,3,0,NULL),
-- Thông báo 4 (Nhân Sự)
(5,4,0,NULL),(6,4,0,NULL);

-- =============================================
-- 15. SYSTEM LOGS
-- =============================================
INSERT INTO system_logs (user_id, action, description, created_at) VALUES
                                                                       (1,    'LOGIN',      'Đăng nhập từ 192.168.1.1',                        NOW()-INTERVAL 1 DAY),
                                                                       (2,    'LOGIN',      'Đăng nhập từ 192.168.1.2',                        NOW()-INTERVAL 1 DAY),
                                                                       (3,    'LOGIN',      'Đăng nhập từ 192.168.1.3',                        NOW()-INTERVAL 1 DAY),
                                                                       (1,    'CREATE',     'Tạo nhân viên mới: Phạm Thị Tester',              NOW()-INTERVAL 2 DAY),
                                                                       (2,    'UPDATE',     'Giao task #2 cho Lê Văn Lập Trình',               NOW()-INTERVAL 2 DAY),
                                                                       (5,    'UPDATE',     'Duyệt đơn của Hoàng Văn Nhân Sự',                 NOW()-INTERVAL 3 DAY),
                                                                       (1,    'LOGIN',      'Đăng nhập từ 192.168.1.1',                        NOW()),
                                                                       (2,    'CREATE',     'Tạo thông báo: Họp phòng CNTT định kỳ',           NOW()-INTERVAL 1 DAY),
                                                                       (NULL, 'LOGIN_FAIL', 'Đăng nhập thất bại email: hacker@evil.com',       NOW()-INTERVAL 12 HOUR);

-- =============================================
-- KIỂM TRA  ← bỏ leave_balance và leave_requests
-- =============================================
-- SELECT 'departments'        AS tabel, COUNT(*) AS rows FROM departments
-- UNION ALL SELECT 'users',             COUNT(*) FROM users
--           UNION ALL SELECT 'employees',         COUNT(*) FROM employees
--           UNION ALL SELECT 'attendance',        COUNT(*) FROM attendance
--           UNION ALL SELECT 'tasks',             COUNT(*) FROM tasks
--           UNION ALL SELECT 'task_updates',      COUNT(*) FROM task_updates
--           UNION ALL SELECT 'requests',          COUNT(*) FROM requests
--           UNION ALL SELECT 'chat_rooms',        COUNT(*) FROM chat_rooms
--           UNION ALL SELECT 'chat_room_members', COUNT(*) FROM chat_room_members
--           UNION ALL SELECT 'messages',          COUNT(*) FROM messages
--           UNION ALL SELECT 'notifications',     COUNT(*) FROM notifications
--           UNION ALL SELECT 'user_notifications',COUNT(*) FROM user_notifications
--           UNION ALL SELECT 'system_logs',       COUNT(*) FROM system_logs;