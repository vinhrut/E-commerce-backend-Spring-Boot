# Entity Relationship Diagram (ERD)

Sơ đồ ERD thể hiện các thực thể (bảng) trong cơ sở dữ liệu và mối quan hệ giữa chúng.
Hệ thống sử dụng cơ sở dữ liệu **PostgreSQL** (hoặc tương đương ở môi trường SQL Server/MySQL). Các khóa chính (PK) và khóa ngoại (FK) được chú thích rõ ràng.

```mermaid
erDiagram
    users {
        varchar id PK
        varchar full_name
        varchar email
        varchar password
        varchar phone
        varchar role
        boolean lock_user
        timestamp created_at
        timestamp updated_at
    }

    products {
        varchar id PK
        varchar name
        float price
        text description
        varchar type
        varchar material
        boolean deleted
        timestamp created_at
    }

    product_sizes {
        bigint id PK
        varchar product_id FK
        varchar size_name
        integer quantity "Quản lý tồn kho theo size"
    }

    orders {
        bigint id PK
        varchar user_id FK
        float total_price
        varchar order_status
        timestamp order_date
    }

    order_items {
        bigint id PK
        bigint order_id FK
        varchar product_id FK
        integer quantity
        float price
    }

    shipping_info {
        bigint id PK
        varchar user_id FK
        varchar full_name
        varchar phone
        varchar province
        varchar district
        varchar ward
        varchar street_address
        boolean is_default
    }

    cart {
        bigint id PK
        varchar user_id FK "1 user có 1 cart duy nhất"
    }

    cart_items {
        bigint id PK
        bigint cart_id FK
        varchar product_id FK
        integer quantity
    }

    payments {
        bigint id PK
        bigint order_id FK "Liên kết thanh toán với đơn hàng"
        varchar txn_ref "Mã tham chiếu VNPay"
        bigint amount
    }

    invoices {
        bigint id PK
        bigint order_id FK
    }

    refresh_tokens {
        bigint id PK
        varchar user_id FK
        varchar token
    }

    %% Relationships Definition
    users ||--o{ shipping_info : "có địa chỉ giao hàng"
    users ||--o{ orders : "đặt các đơn hàng"
    users ||--|| cart : "sở hữu giỏ hàng"
    users ||--o{ refresh_tokens : "quản lý phiên đăng nhập"
    
    cart ||--o{ cart_items : "chứa sản phẩm"
    products ||--o{ cart_items : "được thêm vào"
    
    orders ||--o{ order_items : "bao gồm chi tiết"
    products ||--o{ order_items : "nằm trong đơn hàng"
    products ||--o{ product_sizes : "chia theo size (Kho)"
    
    orders ||--|| payments : "được thanh toán"
    orders ||--|| invoices : "xuất hóa đơn"
```

### Mô tả chi tiết Khóa Ngoại (Foreign Keys)
- `shipping_info.user_id` -> Tham chiếu tới bảng `users.id` (1 User có nhiều địa chỉ).
- `cart.user_id` -> Tham chiếu tới bảng `users.id` (Mỗi User sở hữu 1 giỏ hàng duy nhất).
- `cart_items.cart_id` -> Tham chiếu tới bảng `cart.id`.
- `cart_items.product_id` -> Tham chiếu tới bảng `products.id`.
- `orders.user_id` -> Tham chiếu tới bảng `users.id`.
- `order_items.order_id` -> Tham chiếu tới bảng `orders.id`.
- `order_items.product_id` -> Tham chiếu tới bảng `products.id`.
- `product_sizes.product_id` -> Tham chiếu tới bảng `products.id` (Quản lý số lượng tồn kho theo thuộc tính kích cỡ).
- `payments.order_id` -> Tham chiếu tới bảng `orders.id` (Thanh toán qua VNPay).
- `invoices.order_id` -> Tham chiếu tới bảng `orders.id`.
- `refresh_tokens.user_id` -> Tham chiếu tới bảng `users.id`.
