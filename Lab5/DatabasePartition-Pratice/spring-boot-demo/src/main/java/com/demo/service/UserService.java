package com.demo.service;

import org.springframework.stereotype.Service;

/**
 * ==========================================================
 * HORIZONTAL PARTITIONING (SHARDING THỦ CÔNG - MỨC APPLICATION)
 * ==========================================================
 * 
 * Mô phỏng lại ví dụ của bạn:
 * Nếu là Nam -> Lưu bảng table_user_01
 * Nếu là Nữ -> Lưu bảng table_user_02
 * 
 * Lưu ý: Trong Spring JPA, @Table(name="xxx") thường là tĩnh. 
 * Để lưu bảng động, bạn có thể dùng JdbcTemplate hoặc EntityManager (Native Query)
 * HOẶC thông qua config DynamicRoutingDataSource như file bên cạnh.
 */
@Service
public class UserService {

    // Ví dụ dùng JdbcTemplate thay vì Hibernate vì tên bảng là DOCODE (Dynamic).
    // @Autowired
    // private JdbcTemplate jdbcTemplate; 

    /**
     * Hàm thêm mới người dùng chứa logic định tuyến (Routing Logic).
     * @param username Tên 
     * @param gender Giới tính (1: Nam, 2: Nữ)
     */
    public void registerUser(String username, int gender) {

        String targetTable = "";
        
        // ============================================
        // 1. NGHIỆP VỤ ĐỊNH TUYẾN (SHARDING LOGIC)
        // ============================================
        if (gender == 1) {
            targetTable = "table_user_01"; // Ghi vào bảng 1 (Nam)
        } else if (gender == 2) {
            targetTable = "table_user_02"; // Ghi vào bảng 2 (Nữ)
        } else {
            throw new IllegalArgumentException("Khong xac dinh gioi tinh");
        }

        // ============================================
        // 2. THỰC THI TRUY VẤN XUỐNG CƠ SỞ DỮ LIỆU
        // ============================================
        String sql = "INSERT INTO " + targetTable + " (FullName, CreatedDate) VALUES (?, GETDATE())";
        
        // jdbcTemplate.update(sql, username);
        System.out.println(">> Đã lưu [" + username + "] vào BẢNG/PHÂN VÙNG: " + targetTable);

        /**
         * LỢI ÍCH (PERFORMANCE):
         * Nếu bảng 01 và 02 nằm trên 2 Database Vật lý riêng biệt (Functional Split):
         * DatabaseContextHolder.setEnvironment("MALE_DB_SERVER_1");
         * UserRepository.save(user); // Sẽ tự map sang Server của Nam
         * => Nhân đôi sức mạnh ghi (Write Capability) của Hệ thống!
         */
    }
}
