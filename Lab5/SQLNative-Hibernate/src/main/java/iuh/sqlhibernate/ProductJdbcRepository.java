package iuh.sqlhibernate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * ═══════════════════════════════════════════════════════════════════
 *  APPROACH 1: SQL NATIVE – JdbcTemplate (Spring JDBC)
 *
 *  Phải tự viết SQL thuần, tự map ResultSet → Object (RowMapper).
 *  Không có magic auto-generate. Toàn quyền kiểm soát SQL.
 * ═══════════════════════════════════════════════════════════════════
 */
@Repository
@Slf4j
@RequiredArgsConstructor
public class ProductJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    // RowMapper: map 1 row của ResultSet → Product object
    private final RowMapper<Product> ROW_MAPPER = (rs, rowNum) -> {
        Product p = new Product();
        p.setId(rs.getLong("id"));
        p.setName(rs.getString("name"));
        p.setCategory(rs.getString("category"));
        p.setPrice(rs.getDouble("price"));
        p.setStock(rs.getInt("stock"));
        return p;
    };

    // ── CREATE ────────────────────────────────────────────────────────────────
    public Product insert(Product product) {
        String sql = "INSERT INTO products (name, category, price, stock) VALUES (?, ?, ?, ?)";
        log.info("[JDBC] SQL: {} | params={},{},{},{}", sql,
                product.getName(), product.getCategory(), product.getPrice(), product.getStock());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, product.getName());
            ps.setString(2, product.getCategory());
            ps.setDouble(3, product.getPrice());
            ps.setInt(4, product.getStock());
            return ps;
        }, keyHolder);

        product.setId(keyHolder.getKey().longValue());
        return product;
    }

    // ── READ ALL ──────────────────────────────────────────────────────────────
    public List<Product> findAll() {
        String sql = "SELECT * FROM products";
        log.info("[JDBC] SQL: {}", sql);
        return jdbcTemplate.query(sql, ROW_MAPPER);
    }

    // ── READ BY ID ────────────────────────────────────────────────────────────
    public Optional<Product> findById(Long id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        log.info("[JDBC] SQL: {} | id={}", sql, id);
        List<Product> result = jdbcTemplate.query(sql, ROW_MAPPER, id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    // ── READ BY CATEGORY ──────────────────────────────────────────────────────
    public List<Product> findByCategory(String category) {
        String sql = "SELECT * FROM products WHERE category = ?";
        log.info("[JDBC] SQL: {} | category={}", sql, category);
        return jdbcTemplate.query(sql, ROW_MAPPER, category);
    }

    // ── SEARCH BY NAME (LIKE) ─────────────────────────────────────────────────
    public List<Product> searchByName(String keyword) {
        String sql = "SELECT * FROM products WHERE name LIKE ?";
        log.info("[JDBC] SQL: {} | keyword=%{}%", sql, keyword);
        return jdbcTemplate.query(sql, ROW_MAPPER, "%" + keyword + "%");
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    public int update(Product product) {
        String sql = "UPDATE products SET name=?, category=?, price=?, stock=? WHERE id=?";
        log.info("[JDBC] SQL: {} | id={}", sql, product.getId());
        return jdbcTemplate.update(sql,
                product.getName(), product.getCategory(),
                product.getPrice(), product.getStock(), product.getId());
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    public int deleteById(Long id) {
        String sql = "DELETE FROM products WHERE id = ?";
        log.info("[JDBC] SQL: {} | id={}", sql, id);
        return jdbcTemplate.update(sql, id);
    }

    // ── AGGREGATE (SQL thuần) ─────────────────────────────────────────────────
    public double getAveragePrice() {
        String sql = "SELECT AVG(price) FROM products";
        log.info("[JDBC] SQL: {}", sql);
        Double avg = jdbcTemplate.queryForObject(sql, Double.class);
        return avg != null ? avg : 0;
    }

    public long countByCategory(String category) {
        String sql = "SELECT COUNT(*) FROM products WHERE category = ?";
        log.info("[JDBC] SQL: {} | category={}", sql, category);
        Long count = jdbcTemplate.queryForObject(sql, Long.class, category);
        return count != null ? count : 0;
    }
}
