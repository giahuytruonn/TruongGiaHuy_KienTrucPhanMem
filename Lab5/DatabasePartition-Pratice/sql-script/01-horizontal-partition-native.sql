-- ==========================================================
-- 1. DATABASE PARTITIONING (HORIZONTAL) - NATIVE SQL SERVER
-- ==========================================================
-- Kỹ thuật này chia bảng ra thành nhiều vùng (partitions) ở mức vật lý.
-- Ví dụ: Phân chia theo Giới tính (GenderID: 1 = Nam, 2 = Nữ) 
-- hoặc Phân chia theo Năm sinh (phổ biến hơn cho Horizontal).
-- Dưới đây demo theo "Giới tính" như yêu cầu của bạn, tuy trong thực tế
-- phân vùng theo ngày tháng (Time-based Data) sẽ tối ưu dung lượng nhất.

USE master;
GO
CREATE DATABASE PartitionDemoDB;
GO
USE PartitionDemoDB;
GO

-- Bước 1: Tạo các Filegroups (Thư mục vật lý lưu trữ)
ALTER DATABASE PartitionDemoDB ADD FILEGROUP FG_MALE;
ALTER DATABASE PartitionDemoDB ADD FILEGROUP FG_FEMALE;
GO

-- Thêm các file vật lý (.ndf) vào từng Filegroup
ALTER DATABASE PartitionDemoDB 
ADD FILE (NAME = N'Male_Data', FILENAME = N'C:\SQLData\Male_Data.ndf', SIZE = 5MB, MAXSIZE = 100MB, FILEGROWTH = 5MB)
TO FILEGROUP FG_MALE;
GO

ALTER DATABASE PartitionDemoDB 
ADD FILE (NAME = N'Female_Data', FILENAME = N'C:\SQLData\Female_Data.ndf', SIZE = 5MB, MAXSIZE = 100MB, FILEGROWTH = 5MB)
TO FILEGROUP FG_FEMALE;
GO

-- Bước 2: Tạo Partition Function (Quy tắc cắt dữ liệu)
-- Cắt theo giá trị GenderID: 1 (Nam) và 2 (Nữ)
CREATE PARTITION FUNCTION PF_GenderSplit (INT)
AS RANGE RIGHT FOR VALUES (2); -- Nhỏ hơn 2 (tức là 1) vào vùng 1, Từ 2 trở lên vào vùng 2
GO

-- Bước 3: Tạo Partition Scheme (Ánh xạ vùng cắt vào Filegroup vật lý)
CREATE PARTITION SCHEME PS_GenderSplit
AS PARTITION PF_GenderSplit
TO (FG_MALE, FG_FEMALE); -- Vùng 1 vào FG_MALE, Vùng 2 (từ 2 trở đi) vào FG_FEMALE
GO

-- Bước 4: Tạo Bảng sử dụng Partition Scheme này
CREATE TABLE Users_Horizontal (
    UserID INT IDENTITY(1,1) NOT NULL,
    FullName NVARCHAR(100),
    GenderID INT NOT NULL, -- 1 = Nam, 2 = Nữ
    CreatedDate DATETIME DEFAULT GETDATE(),
    PRIMARY KEY CLUSTERED (UserID, GenderID) -- Partition Column phải nằm trong PK
) ON PS_GenderSplit(GenderID); -- Chỉ định lưu theo Scheme vừa tạo
GO

-- ================= TESTS =================
-- Khi bạn INSERT vào Bảng Users_Horizontal, SQL Server sẽ TỰ ĐỘNG đẩy
-- dữ liệu của Nam vào File ổ cứng FG_MALE, của Nữ vào FG_FEMALE.
INSERT INTO Users_Horizontal (FullName, GenderID) VALUES (N'Truong Gia Huy', 1);
INSERT INTO Users_Horizontal (FullName, GenderID) VALUES (N'Nguyen Thi A', 2);
INSERT INTO Users_Horizontal (FullName, GenderID) VALUES (N'Tran Van B', 1);

-- Kiểm tra xem dữ liệu Nam/Nữ đang nằm vật lý ở ổ đĩa/phân vùng nào.
-- Performance siêu việt vì SQL lấy ra dữ liệu Nam chỉ phải quét đúng ổ đĩa / filegroup đó.
SELECT 
    p.partition_number, 
    f.name AS filegroup_name, 
    p.rows AS row_count
FROM sys.partitions p
JOIN sys.destination_data_spaces dds ON p.partition_number = dds.destination_id
JOIN sys.filegroups f ON dds.data_space_id = f.data_space_id
WHERE p.object_id = OBJECT_ID('Users_Horizontal');
GO
