# E-commerce Backend (Spring Boot)

Đây là dự án Backend cho hệ thống E-commerce, được phát triển trên Framework **Spring Boot**, tích hợp hệ quản trị cơ sở dữ liệu **PostgreSQL**, quản lý tệp độc lập bằng **MinIO** và kết nối dịch vụ thanh toán trực tuyến **VNPay**.

---

## 🚀 1. Các Yêu Cầu Hệ Thống (Prerequisites)
Để có thể biên dịch và chạy cấu hình ứng dụng ở môi trường môi trường máy tính của bạn, vui lòng đảm bảo bạn đã cài đặt:
1. **Java JDK 17** (hoặc mới hơn).
2. **Maven 3.8+** (nếu không chạy lệnh qua wrapper `mvnw`).
3. **PostgreSQL 14+**.
4. **MinIO Server** (dùng cho lưu trữ và upload ảnh).

---

## ⚙️ 2. Hướng Dẫn Cấu Hình (Configuration)

Tất cả cấu hình hệ thống được đặt trong đường dẫn `src/main/resources/application.yml`. 
Các cấu hình mặc định đang được cung cấp như sau:

### 2.1 Cấu hình PostgreSQL (Database)
Bạn cần tạo sẵn một Database trắng mang tên `ecommerce` ở local:
```sql
CREATE DATABASE ecommerce;
```
Tài khoản và mật khẩu mặc định (có thể thay đổi trong file `application.yml`):
- URL kết nối: `jdbc:postgresql://localhost:5432/ecommerce`
- Username: `admin`
- Password: `admin`

*Framework Spring Data JPA, Hibernate được cài đặt ở chế độ `ddl-auto: update`, các bảng dữ liệu sẽ tự sinh hoặc cập nhật schema khi Server bật lên.*

### 2.2 Cấu hình MinIO (Storage)
Đảm bảo MinIO Server của bạn đã được bật và cấp quyền. Chạy lệnh hoặc Docker MinIO tại port mặc định `9000`. Cấu hình trong file yml là:
- URL: `http://127.0.0.1:9000`
- AccessKey/SecretKey: `minioadmin` / `minioadmin`
- Bucket chỉ định: `ecommerce` (Server sẽ tự ném ảnh vào bucket này).

### 2.3 Tham Số Thanh Toán VNPay
Hệ thống kết nối luồng Sandbox (`https://sandbox.vnpayment.vn`) của VNPay cho tác vụ Demo.
- Tính năng Return URL trả về React: `http://localhost:5173/vnpay-return` (Frontend).
- Hãy sửa url lại nếu bạn đổi port frontend.

---

## ▶️ 3. Cách Bật Cụm Máy Chủ (Run Backend)

Sau khi các Database (PostgreSQL) và Middleware (MinIO) ở trạng thái Running, bạn có thể chạy máy chủ trực tiếp thông qua hai phương pháp:

**Cách 1: Sử dụng Maven Wrapper tích hợp sẵn**
Mở thư mục gốc của Backend tại Terminal/CMD/PowerShell và chạy:
```bash
# Đối với hệ điều hành Windows:
.\mvnw.cmd spring-boot:run

# Đối với Mac/Linux:
./mvnw spring-boot:run
```

**Cách 2: Build file JAR và triển khai**
```bash
# Build trước (bỏ qua unit test nếu cần)
.\mvnw.cmd clean package -DskipTests

# Chạy file ở module target/
java -jar target/ecommerce-0.0.1-SNAPSHOT.jar
```

*Ứng dụng sẽ tự động start các cấu hình và lắng nghe ở thư mục ảo Tomcat mặc định tại cổng `http://localhost:8080` (trừ khi có điều chỉnh `server.port`).*

---

## 📐 4. Tài Liệu Thiết Kế Kỹ Thuật (Docs)
Để hiểu rõ hệ thống hoạt động như thế nào, tham khảo sơ đồ:
- **[Sơ đồ thiết kế Cơ sở dữ liệu - ERD](docs/ERD.md)**: Hiển thị minh họa rõ cấu trúc Data Schema với PK, FK (Product, Order, Invoice, Shipping...).
- **[Sơ đồ kịch bản chạy - Use Cases](docs/Use_Case.md)**: Thể hiện hành vi nghiệp vụ của người dùng với Admin Dashboard.
