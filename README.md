# Fruit Shop Management

Ứng dụng Android quản lý cửa hàng trái cây và đặt hàng trực tuyến, phân quyền theo vai trò **Admin** và **Customer**.

## 1. Công nghệ sử dụng

- **Ngôn ngữ**: Java
- **Giao diện**: Android XML Layout (`AppCompatActivity`, `LinearLayout`, `ListView`...)
- **Cơ sở dữ liệu**: SQLite (`SQLiteOpenHelper`)
- **Lưu phiên đăng nhập**: `SharedPreferences`

## 2. Cơ sở dữ liệu

Hệ thống gồm 4 bảng chính trong SQLite (`fruitshop.db`):

- **`Account`**: `id`, `username`, `password`, `role` (`ADMIN` / `CUSTOMER`)
- **`Fruit`**: `id`, `name`, `category`, `price`, `quantity`, `expiryDate`, `description`
- **`Customer`**: `id`, `fullname`, `phone`, `email`, `accountId`
- **`Order`**: `id`, `customerId`, `orderDate`, `totalAmount`, `status` (`Pending` / `Confirmed` / `Completed`)

## 3. Chức năng chính

### Đăng nhập & Tài khoản
- Đăng nhập phân quyền theo vai trò **Admin** và **Customer**.
- Hỗ trợ ghi nhớ đăng nhập bằng `SharedPreferences` và chức năng đăng xuất.

### Vai trò Admin
- **Quản lý trái cây**: Xem danh sách, thêm mới, cập nhật, xoá và tìm kiếm trái cây theo tên hoặc loại.
- **Quản lý hạn sử dụng**: Hiển thị hạn sử dụng và cảnh báo màu đỏ đối với trái cây đã hết hạn.
- **Quản lý đơn hàng**: Xem toàn bộ đơn hàng của khách hàng và cập nhật trạng thái đơn (`Pending`, `Confirmed`, `Completed`).

### Vai trò Customer
- **Xem & Tìm kiếm trái cây**: Xem danh sách các loại trái cây còn hàng, tra cứu hạn sử dụng và tìm kiếm theo tên/loại.
- **Đặt hàng**: Đặt mua trái cây (hệ thống tự động chặn đặt hàng đối với trái cây đã hết hạn hoặc vượt quá số lượng tồn kho).
- **Lịch sử đơn hàng**: Theo dõi danh sách các đơn hàng đã đặt và trạng thái xử lý.

## 4. Cấu trúc thư mục

```
app/src/main/
├── java/com/example/fruitshop/
│   ├── activities/
│   │   ├── LoginActivity.java
│   │   ├── FruitActivity.java
│   │   └── OrderActivity.java
│   ├── dal/
│   │   └── DBHelper.java
│   ├── models/
│   │   ├── Account.java
│   │   ├── Customer.java
│   │   ├── Fruit.java
│   │   └── Order.java
│   └── MainActivity.java
└── res/layout/
    ├── activity_login.xml
    ├── activity_main.xml
    ├── activity_fruit.xml
    └── activity_order.xml
```

## 5. Phân công thành viên

| Thành viên | Nhánh | Phụ trách |
|---|---|---|
| **Thắng** | `feature/models` | Xây dựng các lớp Model (`Account`, `Customer`, `Fruit`, `Order`) |
| **Trường** | `feature/database` | Xây dựng `DBHelper` (khởi tạo bảng SQLite, dữ liệu mẫu, truy vấn & CRUD) |
| **Hiếu** | `feature/login` | Chức năng đăng nhập (`LoginActivity`), `SharedPreferences` và màn hình chính (`MainActivity`) |
| **Khang** | `feature/fruit-order` | Chức năng quản lý/đặt trái cây (`FruitActivity`) và quản lý/lịch sử đơn hàng (`OrderActivity`) |

## 6. Tài khoản chạy thử (Dữ liệu mẫu)

| Vai trò | Username | Password |
|---|---|---|
| **Admin** | `admin` | `123` |
| **Customer 1** | `customer` | `123` |
| **Customer 2** | `customer2` | `123` |

## 7. Hướng dẫn cài đặt & chạy dự án

1. Mở dự án bằng **Android Studio**.
2. Đợi Gradle đồng bộ hoàn tất (hoặc chạy `./gradlew assembleDebug`).
3. Chọn máy ảo (Emulator) hoặc thiết bị thật và nhấn **Run (▶)**.
