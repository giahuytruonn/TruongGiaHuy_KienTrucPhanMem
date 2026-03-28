-- ==========================================================
-- 2. VERTICAL PARTITIONING - SQL SCRIPT
-- ==========================================================
-- Kỹ thuật này cắt một bảng béo phì (nhiều cột) thành 2 hoặc nhiều bảng mỏng hơn (ít cột).
-- Mục tiêu: Cải thiện hiệu suất bộ nhớ cache (RAM) bằng cách loại bỏ
-- các cột Text dài, BLOB, XML mà rất ít khi được Query hiển thị ngay lập tức.

USE PartitionDemoDB;
GO

-- THAY VÌ 1 Bảng 50 cột chứa Avatar hình ảnh siêu nặng...
-- CREATE TABLE Users_Heavy (
--   UserID INT PRIMARY KEY,
--   Username VARCHAR(50), 
--   PasswordHash VARCHAR(255),
--   Address NVARCHAR(500),
--   Bio NVARCHAR(MAX),
--   AvatarImage VARBINARY(MAX) -- Dữ liệu ảnh cực nặng 
-- );

-- THỰC HIỆN VERTICAL PARTITION:
-- Bảng 1: Bảng Core - Chứa những trường được Load liên tục (Mật độ truy vấn > 90%)
-- Load siêu nhanh, do 1 Data Page chứa được cực kỳ nhiều bảng này.
CREATE TABLE Users_Core_Info (
    UserID INT IDENTITY(1,1) PRIMARY KEY,
    Username VARCHAR(50) NOT NULL,
    PasswordHash VARCHAR(255) NOT NULL,
    IsActive BIT DEFAULT 1
);
GO

-- Bảng 2: Bảng Profile - Các cột Dữ liệu Nặng (Heavy/LOB Data) và ÍT bị Query.
-- (Bao gồm Avatar kích thước lớn, Biography dài).
-- Load siêu chậm nhưng vì ít truy xuất nên không làm chậm hệ thống chính.
CREATE TABLE Users_Profile_Detail (
    UserID INT PRIMARY KEY, -- Share chung ID làm Primary Key 
    Address NVARCHAR(500),
    Bio NVARCHAR(MAX),
    AvatarImageData VARBINARY(MAX),
    CONSTRAINT FK_UserProfile_UserCore FOREIGN KEY (UserID) 
        REFERENCES Users_Core_Info(UserID) ON DELETE CASCADE
);
GO

-- ================= TESTS =================
INSERT INTO Users_Core_Info (Username, PasswordHash) 
VALUES ('admin', 'hashed123');

-- Bảng 2 chỉ được tạo ra khi User cập nhật thông tin chi tiết
INSERT INTO Users_Profile_Detail (UserID, Address, Bio)
VALUES (1, N'123 Quan 1, HCM', N'Đây là mô tả rất rất dài của người làm Admin hệ thống...');
GO

-- Performance test idea:
-- 1. Bạn login hệ thống: Chỉ mất 1 mili-giây: SELECT * FROM Users_Core_Info WHERE Username='admin'
-- 2. Bạn xem chức năng Profile: Tốn 10 mili-giây:
-- SELECT c.Username, p.Address, p.AvatarImageData 
-- FROM Users_Core_Info c JOIN Users_Profile_Detail p ON c.UserID = p.UserID
