package com.demo.config.routing;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * ==========================================================
 * FUNCTIONAL PARTITIONING (hoặc DATABASE SHARDING) IN SPRING
 * ==========================================================
 * 
 * Đây là class chính quyết định truy vấn sẽ chạy vào Database/Server nào.
 * Phù hợp với "Functional Partitioning" nơi Read-Write được tách rời,
 * HOẶC tách "User_DB_01" và "User_DB_02".
 */
public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        // Lấy thông tin ngữ cảnh từ ThreadLocal hiện tại 
        // Ví dụ: ContextHolder đang chứa chuỗi "DB_NAM" hoặc "DB_NU" 
        // hoặc "WRITE_ORDER_DB", "READ_USER_DB".
        return DatabaseContextHolder.getEnvironment(); 
    }
}

/**
 * ThreadLocal để lưu trữ Database Key đang sử dụng cho Request hiện tại.
 */
class DatabaseContextHolder {
    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    public static void setEnvironment(String dbType) {
        CONTEXT.set(dbType);
    }

    public static String getEnvironment() {
        return CONTEXT.get();
    }

    public static void clearEnvironment() {
        CONTEXT.remove();
    }
}
