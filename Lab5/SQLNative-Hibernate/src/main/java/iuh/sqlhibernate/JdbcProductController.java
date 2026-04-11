package iuh.sqlhibernate;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ╔════════════════════════════════════════════════════════╗
 * ║  APPROACH 1: SQL NATIVE (JdbcTemplate)               ║
 * ║  Base URL: /api/jdbc                                  ║
 * ╚════════════════════════════════════════════════════════╝
 *
 * Viết SQL thuần, tự map ResultSet, full control.
 */
@RestController
@RequestMapping("/api/jdbc")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class JdbcProductController {

    private final ProductJdbcRepository jdbcRepo;

    /** GET /api/jdbc/products – lấy tất cả (SQL: SELECT * FROM products) */
    @GetMapping("/products")
    public List<Product> getAll() {
        return jdbcRepo.findAll();
    }

    /** GET /api/jdbc/products/{id} – lấy theo id (SQL: SELECT * FROM products WHERE id=?) */
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return jdbcRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** GET /api/jdbc/products/category/{cat} – lọc theo category */
    @GetMapping("/products/category/{cat}")
    public List<Product> getByCategory(@PathVariable String cat) {
        return jdbcRepo.findByCategory(cat);
    }

    /** GET /api/jdbc/products/search?q=laptop – tìm kiếm tên (LIKE) */
    @GetMapping("/products/search")
    public List<Product> search(@RequestParam String q) {
        return jdbcRepo.searchByName(q);
    }

    /** POST /api/jdbc/products – thêm mới (SQL: INSERT INTO products …) */
    @PostMapping("/products")
    public Product create(@RequestBody Product product) {
        return jdbcRepo.insert(product);
    }

    /** PUT /api/jdbc/products/{id} – cập nhật (SQL: UPDATE products …) */
    @PutMapping("/products/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        int rows = jdbcRepo.update(product);
        return ResponseEntity.ok(Map.of("approach", "SQL NATIVE (JdbcTemplate)", "rowsAffected", rows));
    }

    /** DELETE /api/jdbc/products/{id} – xoá (SQL: DELETE FROM products …) */
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        int rows = jdbcRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("approach", "SQL NATIVE (JdbcTemplate)", "deleted", rows > 0));
    }

    /** GET /api/jdbc/stats – aggregate queries */
    @GetMapping("/stats")
    public Map<String, Object> stats() {
        return Map.of(
                "approach",        "SQL NATIVE (JdbcTemplate)",
                "avgPrice",        jdbcRepo.getAveragePrice(),
                "electronicsCount", jdbcRepo.countByCategory("Electronics")
        );
    }
}
