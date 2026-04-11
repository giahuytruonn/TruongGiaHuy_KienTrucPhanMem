package iuh.sqlhibernate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║   SQL Native (JDBC / JdbcTemplate) vs Hibernate (JPA)      ║
 * ╠══════════════════════════════════════════════════════════════╣
 * ║  Cùng 1 entity Product, cùng 1 bảng "products",            ║
 * ║  nhưng được truy cập bằng 2 cách khác nhau:                ║
 * ║                                                              ║
 * ║  /api/jdbc/*     → JdbcTemplate (SQL thuần)                ║
 * ║  /api/jpa/*      → Spring Data JPA + Hibernate             ║
 * ║                                                              ║
 * ║  H2 Console: http://localhost:8090/h2-console               ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
@SpringBootApplication
public class SqlNativeHibernateApplication {
    public static void main(String[] args) {
        SpringApplication.run(SqlNativeHibernateApplication.class, args);
    }
}
