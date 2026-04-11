package iuh.sqlhibernate;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ╔════════════════════════════════════════════════════════╗
 * ║  APPROACH 2: HIBERNATE / JPA (Spring Data JPA)       ║
 * ║  Base URL: /api/jpa                                   ║
 * ╚════════════════════════════════════════════════════════╝
 *
 * Không viết SQL. Hibernate tự generate. ORM magic.
 */
@RestController
@RequestMapping("/api/jpa")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class JpaProductController {

    private final ProductJpaRepository jpaRepo;

    /** GET /api/jpa/products – Hibernate: SELECT p FROM Product p */
    @GetMapping("/products")
    public List<Product> getAll() {
        return jpaRepo.findAll();
    }

    /** GET /api/jpa/products/{id} – Hibernate: SELECT p FROM Product p WHERE p.id=? */
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return jpaRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** GET /api/jpa/products/category/{cat} – method naming convention → auto SQL */
    @GetMapping("/products/category/{cat}")
    public List<Product> getByCategory(@PathVariable String cat) {
        return jpaRepo.findByCategory(cat);
    }

    /** GET /api/jpa/products/price?max=5000000 – method naming → auto SQL */
    @GetMapping("/products/price")
    public List<Product> getByMaxPrice(@RequestParam double max) {
        return jpaRepo.findByPriceLessThanEqual(max);
    }

    /** GET /api/jpa/products/search?q=laptop – JPQL query */
    @GetMapping("/products/search")
    public List<Product> search(@RequestParam String q) {
        return jpaRepo.searchByName(q);
    }

    /** GET /api/jpa/products/low-stock?min=15 – native SQL trong JPA */
    @GetMapping("/products/low-stock")
    public List<Product> lowStock(@RequestParam(defaultValue = "15") int min) {
        return jpaRepo.findLowStock(min);
    }

    /** POST /api/jpa/products – Hibernate: INSERT INTO products … (auto) */
    @PostMapping("/products")
    public Product create(@RequestBody Product product) {
        return jpaRepo.save(product);
    }

    /** PUT /api/jpa/products/{id} – Hibernate: UPDATE products … (auto) */
    @PutMapping("/products/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product product) {
        return jpaRepo.findById(id).map(existing -> {
            existing.setName(product.getName());
            existing.setCategory(product.getCategory());
            existing.setPrice(product.getPrice());
            existing.setStock(product.getStock());
            return ResponseEntity.ok(jpaRepo.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    /** DELETE /api/jpa/products/{id} – Hibernate: DELETE FROM products … */
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        if (!jpaRepo.existsById(id)) return ResponseEntity.notFound().build();
        jpaRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("approach", "HIBERNATE/JPA", "deleted", true, "id", id));
    }

    /** GET /api/jpa/stats – Spring Data JPA aggregate */
    @GetMapping("/stats")
    public Map<String, Object> stats() {
        long total = jpaRepo.count();
        return Map.of(
                "approach",      "HIBERNATE/JPA (Spring Data JPA)",
                "totalProducts", total,
                "electronicsCount", jpaRepo.findByCategory("Electronics").size()
        );
    }
}
