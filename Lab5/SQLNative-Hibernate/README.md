# Lab 5 – SQL Native (JDBC) vs Hibernate (JPA)

> **Sinh viên:** Trương Gia Huy  
> **Môn:** Kiến Trúc Phần Mềm

---

## 1. Tổng quan

Dự án này demo **cùng 1 entity `Product`**, **cùng 1 database (H2 in-memory)**, nhưng được truy cập bằng **2 cách khác nhau**:

| URL prefix  | Approach                           |
|-------------|------------------------------------|
| `/api/jdbc` | **SQL Native** – `JdbcTemplate`    |
| `/api/jpa`  | **Hibernate/JPA** – Spring Data JPA |

---

## 2. Kiến trúc

```
┌────────────────────────────────────────────────────────────┐
│                   Spring Boot :8090                        │
├──────────────────────┬─────────────────────────────────────┤
│  /api/jdbc/*         │  /api/jpa/*                        │
│                      │                                     │
│  JdbcProductController  JpaProductController               │
│       │                      │                            │
│  ProductJdbcRepository   ProductJpaRepository              │
│  (JdbcTemplate)          (Spring Data JPA)                 │
│  viết SQL thủ công       Hibernate tự generate SQL         │
│       │                      │                            │
│       └──────────┬───────────┘                            │
│                  │                                         │
│            H2 In-Memory DB                                 │
│            (bảng: products)                                │
└────────────────────────────────────────────────────────────┘
```

---

## 3. Cấu trúc project

```
SQLNative-Hibernate/
├── pom.xml
├── src/main/java/iuh/sqlhibernate/
│   ├── SqlNativeHibernateApplication.java    Main class
│   ├── Product.java                          Entity (@Entity + JDBC compatible)
│   │
│   ├── ProductJdbcRepository.java            SQL NATIVE – JdbcTemplate + RowMapper
│   ├── JdbcProductController.java            REST /api/jdbc/*
│   │
│   ├── ProductJpaRepository.java             HIBERNATE – Spring Data JPA
│   └── JpaProductController.java             REST /api/jpa/*
│
└── src/main/resources/
    ├── application.properties                H2 config
    └── data.sql                              Seed data
```

---

## 4. Cách chạy

```bash
cd SQLNative-Hibernate
./mvnw spring-boot:run
```

- App chạy tại: http://localhost:8090
- H2 Console: http://localhost:8090/h2-console
  - JDBC URL: `jdbc:h2:mem:demodb`
  - User: `sa`, Password: _(để trống)_

---

## 5. API Demo – SQL Native vs Hibernate

### 5.1 Lấy tất cả sản phẩm

```bash
# SQL Native (JdbcTemplate)
GET http://localhost:8090/api/jdbc/products
# SQL thực thi: SELECT * FROM products

# Hibernate / JPA
GET http://localhost:8090/api/jpa/products
# SQL Hibernate generate: select p1_0.id,p1_0.category,p1_0.name,p1_0.price,p1_0.stock from products p1_0
```

### 5.2 Thêm sản phẩm mới

```bash
# SQL Native
POST http://localhost:8090/api/jdbc/products
Content-Type: application/json
{
  "name": "Bàn phím cơ Keychron K2",
  "category": "Electronics",
  "price": 1800000,
  "stock": 25
}
# SQL thực thi: INSERT INTO products (name, category, price, stock) VALUES (?, ?, ?, ?)

# Hibernate / JPA
POST http://localhost:8090/api/jpa/products
# (cùng body)
# Hibernate tự generate INSERT, quản lý session, cache
```

### 5.3 Tìm kiếm theo category

```bash
# SQL Native
GET http://localhost:8090/api/jdbc/products/category/Electronics
# SQL: SELECT * FROM products WHERE category = ?

# Hibernate / JPA (method naming convention)
GET http://localhost:8090/api/jpa/products/category/Electronics
# Hibernate generate: select p from Product p where p.category=?
```

### 5.4 Tìm theo giá (chỉ JPA – method naming)

```bash
GET http://localhost:8090/api/jpa/products/price?max=5000000
# Hibernate generate từ method: findByPriceLessThanEqual(double)
```

### 5.5 Tìm kiếm theo tên (LIKE)

```bash
# SQL Native (LIKE thủ công)
GET http://localhost:8090/api/jdbc/products/search?q=laptop

# JPA (JPQL)
GET http://localhost:8090/api/jpa/products/search?q=laptop
# JPQL: SELECT p FROM Product p WHERE p.name LIKE %:keyword%
```

### 5.6 Low stock (JPA với native SQL query)

```bash
GET http://localhost:8090/api/jpa/products/low-stock?min=15
# Dùng @Query(nativeQuery=true): SELECT * FROM products WHERE stock < :minStock
```

### 5.7 Thống kê

```bash
GET http://localhost:8090/api/jdbc/stats
GET http://localhost:8090/api/jpa/stats
```

---

## 6. So sánh chi tiết: SQL Native vs Hibernate

### 6.1 Cách viết CRUD cơ bản

| Thao tác | SQL Native (JdbcTemplate) | Hibernate (JPA) |
|----------|--------------------------|-----------------|
| INSERT   | Viết SQL INSERT thủ công, dùng `KeyHolder` lấy id | `repository.save(entity)` – Hibernate lo hết |
| SELECT   | Viết SQL, dùng `RowMapper` map từng field | `repository.findById(id)` – tự map |
| UPDATE   | Viết SQL UPDATE, set từng field | `repository.save(entity)` sau khi thay đổi field |
| DELETE   | Viết SQL DELETE | `repository.deleteById(id)` |
| WHERE    | Tham số `?` trong PreparedStatement | Method naming / JPQL / @Query |

### 6.2 Code so sánh – SELECT BY CATEGORY

**SQL Native:**
```java
// Phải tự viết SQL + RowMapper
String sql = "SELECT * FROM products WHERE category = ?";
return jdbcTemplate.query(sql, ROW_MAPPER, category);
```

**Hibernate / JPA:**
```java
// Chỉ khai báo method trong interface – Hibernate tự generate
List<Product> findByCategory(String category);
```

### 6.3 Code so sánh – INSERT

**SQL Native:**
```java
String sql = "INSERT INTO products (name, category, price, stock) VALUES (?, ?, ?, ?)";
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
```

**Hibernate / JPA:**
```java
// 1 dòng duy nhất
return jpaRepo.save(product);
```

---

## 7. Bảng so sánh tổng hợp

| Tiêu chí                    | SQL Native (JDBC / JdbcTemplate)        | Hibernate (JPA / Spring Data JPA)         |
|-----------------------------|----------------------------------------|-------------------------------------------|
| **Cách tiếp cận**           | SQL-centric (tập trung vào SQL)        | Object-centric (tập trung vào Object)     |
| **Lượng code**              | Nhiều hơn – phải viết SQL + RowMapper  | Ít hơn – interface + method naming        |
| **Kiểm soát SQL**           | Hoàn toàn – viết gì chạy đúng đó      | Hibernate generate – khó control hoàn toàn |
| **Performance**             | Cao – tối ưu SQL dễ hơn               | Thấp hơn nếu không cẩn thận (N+1, cache) |
| **Learning curve**          | Thấp – ai biết SQL là làm được        | Cao hơn – cần hiểu JPA, Hibernate lifecycle |
| **Portability**             | Kém – SQL phụ thuộc database           | Tốt – JPQL không phụ thuộc database       |
| **Mapping complex joins**   | Dễ – viết JOIN như bình thường        | Phức tạp – @OneToMany, @ManyToMany, fetch |
| **Lazy/Eager loading**      | Không có – tự kiểm soát               | Có – nhưng dễ gây N+1 problem             |
| **Caching**                 | Tự implement                           | Hibernate có L1/L2 cache tích hợp         |
| **Transaction**             | Tự quản lý                            | @Transactional – Spring quản lý           |
| **Phù hợp với**             | Hệ thống cần performance cao, report SQL phức tạp | CRUD thông thường, business domain rõ ràng |

---

## 8. Khi nào dùng cái nào?

### Dùng SQL Native (JdbcTemplate) khi:
- Cần **query phức tạp**: nhiều JOIN, GROUP BY, HAVING, subquery
- Cần **tối ưu performance** tuyệt đối – biết chính xác SQL nào chạy
- **Batch processing** lớn – insert hàng triệu record nhanh
- Team quen với SQL hơn là ORM
- Database có stored procedures, triggers

### Dùng Hibernate / JPA khi:
- CRUD đơn giản, **ít logic phức tạp**
- Cần **portability** – chạy được trên nhiều loại DB
- **Domain model phức tạp** với nhiều quan hệ (1-N, N-N)
- Cần **audit trail** (@CreatedDate, @ModifiedDate)
- Muốn **tốc độ phát triển nhanh** (ít code hơn)

### Thực tế trong dự án lớn: **Dùng cả hai!**
```
Hibernate/JPA  →  CRUD thông thường, quan hệ entity
JdbcTemplate   →  Report queries phức tạp, batch processing
```
