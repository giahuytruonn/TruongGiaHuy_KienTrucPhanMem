-- ============================================================
-- Service-Based Architecture Demo – Shared Database Init
-- Database: service_base_db  (MariaDB / MySQL)
-- ============================================================

CREATE DATABASE IF NOT EXISTS service_base_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE service_base_db;

-- ── Orders table (managed by OrderService) ───────────────────
CREATE TABLE IF NOT EXISTS orders (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    product_name VARCHAR(255) NOT NULL,
    quantity     INT          NOT NULL DEFAULT 1,
    price        DOUBLE       NOT NULL DEFAULT 0,
    status       VARCHAR(50)  NOT NULL DEFAULT 'PENDING',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── Payments table (managed by PaymentService) ───────────────
CREATE TABLE IF NOT EXISTS payments (
    id        BIGINT  NOT NULL AUTO_INCREMENT,
    order_id  BIGINT  NOT NULL,
    amount    DOUBLE  NOT NULL DEFAULT 0,
    status    VARCHAR(50) NOT NULL DEFAULT 'PROCESSING',
    PRIMARY KEY (id),
    INDEX idx_payments_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── Shipments table (managed by ShippingService) ─────────────
CREATE TABLE IF NOT EXISTS shipments (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    order_id        BIGINT       NOT NULL,
    tracking_number VARCHAR(100) NOT NULL,
    status          VARCHAR(50)  NOT NULL DEFAULT 'PREPARING',
    address         VARCHAR(255),
    PRIMARY KEY (id),
    INDEX idx_shipments_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── Sample seed data (optional) ──────────────────────────────
-- INSERT INTO orders (product_name, quantity, price, status)
-- VALUES ('Pizza Margherita', 2, 12.50, 'PENDING');
