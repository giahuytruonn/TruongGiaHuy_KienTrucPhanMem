# Phân Vùng Cơ Sở Dữ Liệu (Database Partitioning) & Sharding

Tài liệu này giải thích chi tiết về kỹ thuật Phân vùng Cơ sở dữ liệu (Database Partitioning), cụ thể trên hệ quản trị SQL Server, cách nó giúp tăng hiệu suất (performance) và cung cấp các ví dụ thực tế. Đặc biệt tập trung vào việc áp dụng kiến trúc này trong Spring Boot (Routing, Phân vùng theo Chức năng).

## 1. Database Partitioning là gì? Tại sao lại giúp tăng Performance?

Database Partitioning là kỹ thuật chia một bảng (table) hoặc một cơ sở dữ liệu lớn thành các phần nhỏ hơn, dễ quản lý hơn. Dữ liệu được chia nhỏ về mặt vật lý, nhưng ở mức logic, ứng dụng hoặc người dùng vẫn có thể thực hiện truy vấn như bình thường.

### Tại sao tăng Performance?
- **Giảm lượng dữ liệu phải quét (Partition Pruning)**: Khi truy vấn có chứa khóa phân vùng (Partition Key), SQL Server (hoặc ORM) chỉ cần tìm trong đúng vùng chứa dữ liệu đó thay vì quét Index (Scan) của toàn bộ dữ liệu.
- **Tách biệt I/O Storage**: Các phân vùng có thể đặt trên các ổ cứng vật lý / thư mục (Filegroups) khác nhau để cân bằng tải đọc/ghi I/O.
- **Giảm tranh chấp khóa (Lock Contention)**: Các thao tác ghi/cập nhật diễn ra độc lập trên các vùng dữ liệu vật lý riêng biệt, tránh khóa toàn bộ cấu trúc (Table lock).
- **Table size nhỏ (Caching ưu việt)**: Cấu trúc bộ nhớ cache / data page trong RAM của DB hoạt động hiệu quả hơn.

---

## 2. Các Loại Phân Vùng & Ví Dụ (Horizontal, Vertical, Function)

### 2.1. Horizontal Partitioning (Phân Vùng Ngang - Theo Row)
Dữ liệu phân nhánh thành các bảng (hoặc phân mảnh bên dưới) có **cấu trúc y hệt nhau**, nhưng **khác nhau về tập dữ liệu (rows)** dựa trên 1 điều kiện.

> **Ví dụ của bạn: Phân chia theo Giới Tính (Nam: table_01, Nữ: table_02)**
> - `table_user_01` lưu thông tin User nam.
> - `table_user_02` lưu thông tin User nữ.

*Thực tế:* Thường phân vùng theo **Thời gian (Date)** (dữ liệu tháng này, dữ liệu tháng trước), theo **Khu vực (Region)** (US, Châu Á), hoặc theo Tên/ID. Do tỷ lệ Nam/Nữ thường là 50/50, tách kiểu này gọi là Sharding theo logic (Application Level).

Có hai cách triển khai:
1. **Mức Database (SQL Native Partitioning)**: Bạn vẫn chỉ có 1 bảng `Users`, nhưng SQL Server tự tách các Page vật lý ra nhiều File khác nhau theo config `Partition Function`.
2. **Mức Application (Sharding)**: Bạn thực sự tạo ra 2 bảng riêng biệt, và dùng Spring Boot (logic code) quyết định xem Insert/Select vào bảng nào (Dùng `@Table` động hoặc `RoutingDataSource`).

### 2.2. Vertical Partitioning (Phân Vùng Dọc - Theo Column)
Chia một bảng chứa rất nhiều cột (đặc biệt là cột Text (max), BLOB chứa ảnh) thành các bảng nhỏ hơn chứa ít cột, liên kết bằng Foreign Key (thường là Quan hệ 1-1).

> **Ví dụ thực tế:**
> Bảng `User` ban đầu có 50 cột, cực kỳ nặng. Ta chia làm 2 bảng:
> 1. `users_core` (ID, Username, Password, Status): Bảng này cực nhỏ, rất nhanh. Được truy vấn liên tục khi User đăng nhập.
> 2. `users_profile` (ID, Address, Bio, Avatar_Image_Blob_Data, Preferences): Bảng này rất nặng, nhưng ít truy cập (Chỉ khi User click vào mục "Xem thông tin chi tiết").

**Tăng Performance ở điểm nào?**
Một trang dữ liệu (Page) trong SQL Server là 8KB. Nếu chia nhỏ bảng theo cột, một Page của `users_core` có thể lưu hàng trăm rows thay vì chỉ vài chục rows -> DB đọc 1 tệp block duy nhất có thể Cache đưa vào RAM được 100 User thay vì 10 User -> Tốc độ quét thông tin đăng nhập siêu nhanh.

### 2.3. Functional Partitioning (Phân Vùng Theo Chức Năng / Nghiệp Vụ)
Đây là bước tiền đề cho kiến trúc **Microservices**. Thay vì mọi dữ liệu nhồi chung vào 1 DB Server lớn `Monolithic_DB`, chúng ta để các cụm dữ liệu độc lập với nhau theo Chức năng (Function).

> **Ví dụ thực tế:**
> - `ECommerce_Order_DB`: Gồm các bảng về Đơn hàng, Lịch sử giao dịch (Chịu tải Write lớn).
> - `ECommerce_User_DB`: Gồm các bảng về User, Auth, Permission (Chịu tải Read lớn).
> - `ECommerce_Product_DB`: Gồm Catalog, Tồn kho (Cache liên tục).

**Tăng Performance ở điểm nào?**
Hai module khác biệt không bị cạnh tranh tài nguyên CPU/RAM/Throttling của DatabaseServer. Khi BlackFriday, người ta mua hàng (Order DB Write Full 100%) thì dịch vụ Đăng nhập (User DB) vẫn chạy trơn tru vì nằm ở Database/Server khác.

---

## 3. Bản Demo (SQL Scripts & Spring Boot)

Tôi đã tạo các thư mục mã nguồn minh họa cho hệ thống này nhằm mô phỏng lại các kỹ thuật:

1. Thư mục `sql-script/`: Chứa các script SQL Server demo. 
   - `01-horizontal-partition-native.sql`: Kỹ thuật Phân vùng ngang chuẩn của SQL Server.
   - `02-vertical-split.sql`: Phân vùng theo Cột.
2. Thư mục `spring-boot-demo/`: Cách Spring xử lý các bài toán.
   - `DynamicRoutingDataSource.java`: Cách để Spring Boot tự động route dữ liệu vào "DB 01 (cho khách VIP)" hoặc "DB 02 (khách thường)". Hoặc Routing Read/Write chức năng.
   - Lớp `UserService` với kỹ thuật thao tác Horizontal Table.

Vui lòng xem các file tạo ra trong dự án này để nhìn tổng quan toàn diện về mã nguồn.
