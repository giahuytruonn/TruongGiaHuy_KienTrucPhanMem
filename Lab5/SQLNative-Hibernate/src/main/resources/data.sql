-- Seed data – chạy tự động khi khởi động (spring.sql.init.mode=always)
INSERT INTO products (name, category, price, stock) VALUES
  ('Laptop Dell XPS 13', 'Electronics', 25000000, 10),
  ('iPhone 16 Pro', 'Electronics', 30000000, 5),
  ('Sách Kiến Trúc Phần Mềm', 'Books', 150000, 50),
  ('Tai nghe Sony WH-1000XM5', 'Electronics', 8500000, 20),
  ('Chuột Logitech MX Master 3', 'Electronics', 2500000, 30);
