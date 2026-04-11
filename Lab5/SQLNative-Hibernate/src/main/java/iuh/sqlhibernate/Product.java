package iuh.sqlhibernate;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity dùng chung cho cả 2 approach:
 *  - Hibernate/JPA: @Entity mapping đến bảng "products"
 *  - JDBC Native:   JdbcTemplate tự map row → Product thủ công
 */
@Entity
@Table(name = "products")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @ToString
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long   id;

    private String name;
    private String category;
    private double price;
    private int    stock;
}
