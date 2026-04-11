package iuh.sqlhibernate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ═══════════════════════════════════════════════════════
 *  APPROACH 2: HIBERNATE / JPA
 *  Spring Data JPA – không cần viết SQL (tự generate)
 * ═══════════════════════════════════════════════════════
 */
@Repository
public interface ProductJpaRepository extends JpaRepository<Product, Long> {

    // Hibernate tự generate: SELECT * FROM products WHERE category = ?
    List<Product> findByCategory(String category);

    // Hibernate tự generate: SELECT * FROM products WHERE price <= ?
    List<Product> findByPriceLessThanEqual(double price);

    // JPQL (HQL) – giống SQL nhưng dùng tên class/field thay vì table/column
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword%")
    List<Product> searchByName(@Param("keyword") String keyword);

    // Native SQL trong JPA – vẫn có thể dùng khi cần
    @Query(value = "SELECT * FROM products WHERE stock < :minStock", nativeQuery = true)
    List<Product> findLowStock(@Param("minStock") int minStock);
}
