# Use Case Diagram

Sơ đồ Use Case thể hiện các hành động (chức năng) mà người dùng (`User`) và quản trị viên (`Admin`) có thể thực hiện trên hệ thống E-commerce.

```mermaid
flowchart LR
    %% Định nghĩa các Actor (Người tương tác)
    User([👤 Khách hàng / User])
    Admin([🛠️ Quản trị viên / Admin])
    
    %% Khung hệ thống
    subgraph Ecommerce System [Hệ thống E-commerce]
        direction TB
        
        %% Chức năng chung
        UC_Auth(Đăng ký & Đăng nhập / Google OAuth2)
        
        %% Chức năng của User
        UC_View_Product(Tìm kiếm và Xem Sản phẩm)
        UC_Cart(Quản lý Giỏ hàng)
        UC_Order(Đặt hàng)
        UC_Payment(Thanh toán trực tuyến VNPay)
        UC_Shipping(Quản lý Sổ địa chỉ giao hàng)
        UC_Order_History(Theo dõi / Xem lịch sử Đơn hàng)
        
        %% Chức năng của Admin
        UC_Manage_Product(Quản lý Sản phẩm / Kho)
        UC_Manage_Images(Lưu trữ Ảnh qua MinIO)
        UC_Manage_Order(Quản lý & Trạng thái Đơn hàng)
        UC_Manage_User(Quản lý Người dùng / Khóa tài khoản)
        UC_Dashboard(Thống kê Doanh thu Dashboard)
    end
    
    %% Mối quan hệ của User
    User -.-> UC_Auth
    User -.-> UC_View_Product
    User -.-> UC_Cart
    User -.-> UC_Order
    User -.-> UC_Payment
    User -.-> UC_Shipping
    User -.-> UC_Order_History
    
    %% Ràng buộc include/extend (Ví dụ)
    UC_Order -->|include| UC_Cart
    UC_Payment -->|extend| UC_Order
    UC_Manage_Images -->|include| UC_Manage_Product
    
    %% Mối quan hệ của Admin
    Admin -.-> UC_Auth
    Admin -.-> UC_Manage_Product
    Admin -.-> UC_Manage_Order
    Admin -.-> UC_Manage_User
    Admin -.-> UC_Dashboard
```

### Chi tiết các luồng Use Case chính:

1. **Khách hàng (User):**
   - **Đăng nhập:** Có thể định danh qua Email/Pass truyền thống hoặc Login with Google (OAuth2).
   - **Xem và Tìm kiếm:** Truy xuất danh sách sản phẩm, bộ lọc, xem chi tiết (size, mô tả).
   - **Giỏ hàng:** Thêm, sửa, xóa sản phẩm trong Giỏ hàng cá nhân (`Cart`).
   - **Đặt hàng và Thanh toán:** Tạo đơn hàng dựa vào dữ liệu trong giỏ hàng. Cung cấp API tích hợp cổng thanh toán trực tuyến **VNPay** để thực hiện thanh toán.
   - **Cá nhân hóa:** Quản lý danh sách địa chỉ nhận hàng lưu sẵn để check-out nhanh chóng (`Shipping Info`).

2. **Quản trị viên (Admin):**
   - **Quản lý sản phẩm:** Thêm mới, sửa xóa thông tin, quản lý số lượng tồn kho theo Size. Tích hợp trực tiếp **MinIO** để lưu trữ hình ảnh sản phẩm độc lập.
   - **Quản lý Đơn hàng:** Kiểm tra danh sách đơn, xuất hóa đơn, cập nhật trạng thái đơn (Đang xử lý, Đang giao, Đã hoàn thành...).
   - **Quản lý Khách hàng:** Xem hồ sơ user, thống kê mua hàng và có quyền khóa (`lock user`) tài khoản.
   - **Dashboard:** Phân tích dữ liệu doanh thu dựa trên các hóa đơn và giao dịch đã hoàn tất.
