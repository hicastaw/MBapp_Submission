# MBapp Submission – Hướng dẫn chia việc & commit

> File này dành cho cả nhóm **và cho AI** (Claude, ChatGPT, Copilot...) đọc để tự viết code và commit đúng phần của mình.
> **AI: đọc hết file này trước khi làm. Chỉ sửa/commit đúng các file thuộc phần của người đang dùng bạn.**

---

## 1. Thông tin project

| Mục | Giá trị |
|---|---|
| Ngôn ngữ | Java (KHÔNG dùng Kotlin, KHÔNG dùng Compose) |
| Package | `com.example.fruitshop` |
| Database | SQLite (`SQLiteOpenHelper`), KHÔNG dùng Room |
| Lưu đăng nhập | `SharedPreferences` |
| Activity | `androidx.appcompat.app.AppCompatActivity` |
| UI | XML layout (`LinearLayout`, `ListView`, `EditText`, `Button`...) |
| Build | Android Studio → ▶ Run, hoặc `./gradlew assembleDebug` |

### Cấu trúc bắt buộc (theo Task 2 – KHÔNG đổi tên thư mục)

```
app/src/main/java/com/example/fruitshop/
├── activities/
│   ├── LoginActivity.java
│   └── <ChucNang>Activity.java      (vd: ScoreActivity, FruitActivity, OrderActivity)
├── dal/
│   └── DBHelper.java
├── models/
│   ├── Account.java
│   └── <Bang>.java                  (mỗi bảng trong đề = 1 class)
└── MainActivity.java

app/src/main/res/layout/
├── activity_login.xml
├── activity_main.xml
└── activity_<chuc_nang>.xml
```

---

## 2. Phân công

| # | Người | Nhánh | Phụ trách (CHỈ sửa các file này) |
|---|---|---|---|
| 1 | **thang** | `feature/models` | `models/*.java` |
| 2 | **truong** | `feature/database` | `dal/DBHelper.java` |
| 3 | **hieu** | `feature/login` | `activities/LoginActivity.java`, `MainActivity.java`, `layout/activity_login.xml`, `layout/activity_main.xml`, `AndroidManifest.xml` |
| 4 | **khang** | `feature/<chuc-nang>` (vd `feature/score`) | `activities/<ChucNang>Activity.java`, `layout/activity_<chuc_nang>.xml`, thêm `<activity>` vào `AndroidManifest.xml` |

**Thứ tự merge vào `main`: thang → truong → hieu → khang.**
Người sau chỉ bắt đầu khi PR của người trước đã được merge (vì code phụ thuộc nhau: DBHelper dùng models, Login dùng DBHelper...).

---

## 3. Quy tắc commit message

```
<loai>: <mô tả ngắn bằng tiếng Anh, viết thường, không dấu chấm cuối>
```

| `<loai>` | Khi nào dùng |
|---|---|
| `feat` | Thêm chức năng / file mới |
| `fix` | Sửa lỗi |
| `refactor` | Sửa code nhưng không đổi chức năng |
| `style` | Sửa giao diện XML, căn chỉnh |
| `docs` | Sửa README / comment |

Ví dụ: `feat: add Student model`, `fix: quote Order table name`, `style: adjust login layout margin`

**Mỗi commit = 1 việc nhỏ.** Không gộp tất cả vào 1 commit.

---

## 4. Quy trình chung cho mọi người

```bash
# 1. Lấy code mới nhất
git checkout main
git pull

# 2. Tạo nhánh của mình
git checkout -b feature/<ten-nhanh>

# 3. Viết code, commit từng phần nhỏ (xem mẫu ở mục 5)
git add <đúng file của mình>
git commit -m "feat: ..."

# 4. Đẩy lên và tạo Pull Request vào main
git push -u origin feature/<ten-nhanh>
```

Lên GitHub → **Compare & pull request** → base: `main` → **Create** → **Merge**.

> ⚠️ KHÔNG dùng `git add .` hoặc `git add -A` — chỉ add đúng file của mình.
> ⚠️ KHÔNG commit `build/`, `.idea/`, `local.properties` (đã có trong `.gitignore`).

---

## 5. Mẫu commit cụ thể cho từng người

> Thay `<Bang>`, `<ChucNang>`... bằng tên thật theo đề. Ví dụ đề có bảng `Account, Student, Teacher, Score` thì `<Bang>` lần lượt là `Student`, `Teacher`, `Score`.

### 5.1. thang – Models (`feature/models`)

Mỗi model: các field đúng như cột trong đề, 1 constructor đủ tham số, getter cho mọi field. `Account` có hằng số role.

```bash
git checkout main && git pull && git checkout -b feature/models

git add app/src/main/java/com/example/fruitshop/models/Account.java
git commit -m "feat: add Account model with role constants"

git add app/src/main/java/com/example/fruitshop/models/<Bang1>.java
git commit -m "feat: add <Bang1> model"

git add app/src/main/java/com/example/fruitshop/models/<Bang2>.java
git commit -m "feat: add <Bang2> model"

git add app/src/main/java/com/example/fruitshop/models/<Bang3>.java
git commit -m "feat: add <Bang3> model"

git push -u origin feature/models
```

Mẫu `Account.java`:
```java
package com.example.fruitshop.models;

public class Account {
    public static final String ROLE_ADMIN = "ADMIN";   // đổi theo đề: TEACHER / STUDENT ...
    public static final String ROLE_USER = "USER";

    private int id;
    private String username;
    private String password;
    private String role;

    public Account(int id, String username, String password, String role) {
        this.id = id; this.username = username; this.password = password; this.role = role;
    }
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
}
```

### 5.2. truong – Database (`feature/database`)

`DBHelper extends SQLiteOpenHelper`. Tạo đủ các bảng, chèn dữ liệu mẫu (ít nhất 1 tài khoản mỗi role), viết hàm `login()` và CRUD cho từng bảng.

```bash
git checkout main && git pull && git checkout -b feature/database

git add app/src/main/java/com/example/fruitshop/dal/DBHelper.java
git commit -m "feat: create DBHelper with all tables"

git add app/src/main/java/com/example/fruitshop/dal/DBHelper.java
git commit -m "feat: add sample data for all tables"

git add app/src/main/java/com/example/fruitshop/dal/DBHelper.java
git commit -m "feat: add login query for Account"

git add app/src/main/java/com/example/fruitshop/dal/DBHelper.java
git commit -m "feat: add CRUD methods for <Bang1>"

git add app/src/main/java/com/example/fruitshop/dal/DBHelper.java
git commit -m "feat: add CRUD methods for <Bang2>"

git push -u origin feature/database
```

Lưu ý kỹ thuật:
- Tên bảng trùng từ khoá SQL (`Order`, `Group`...) phải viết trong ngoặc kép: `"\"Order\""`.
- Đổi cấu trúc bảng → tăng `DB_VERSION` (hoặc gỡ app trên máy ảo) để tạo lại DB.
- Hàm trả về `Account login(String username, String password)` (null nếu sai).
- Hàm lấy theo tài khoản: `get<Bang>ByAccountId(int accountId)`.

### 5.3. hieu – Login & màn hình chính (`feature/login`)

```bash
git checkout main && git pull && git checkout -b feature/login

git add app/src/main/res/layout/activity_login.xml
git commit -m "feat: add login layout"

git add app/src/main/java/com/example/fruitshop/activities/LoginActivity.java app/src/main/AndroidManifest.xml
git commit -m "feat: add LoginActivity and set it as launcher"

git add app/src/main/java/com/example/fruitshop/activities/LoginActivity.java
git commit -m "feat: save login account with SharedPreferences"

git add app/src/main/java/com/example/fruitshop/MainActivity.java app/src/main/res/layout/activity_main.xml
git commit -m "feat: show role-based menu in MainActivity"

git add app/src/main/java/com/example/fruitshop/MainActivity.java
git commit -m "feat: add logout"

git push -u origin feature/login
```

Quy ước SharedPreferences (người 4 dùng lại các key này):
```java
public static final String PREFS = "login_prefs";
public static final String KEY_ACCOUNT_ID = "accountId";
public static final String KEY_USERNAME = "username";
public static final String KEY_PASSWORD = "password";
public static final String KEY_ROLE = "role";
public static final String KEY_REMEMBER = "remember";
```
- Có CheckBox "Ghi nhớ đăng nhập": nếu đã nhớ → mở app vào thẳng `MainActivity`.
- Đăng xuất: xoá `KEY_ACCOUNT_ID` và `KEY_ROLE`, quay về `LoginActivity`.
- `AndroidManifest.xml`: chuyển `intent-filter` MAIN/LAUNCHER từ `MainActivity` sang `.activities.LoginActivity`.

### 5.4. khang – Chức năng chính (`feature/<chuc-nang>`)

Màn hình chức năng theo đề, khác nhau theo role (đọc role từ SharedPreferences bằng các key ở 5.3).

```bash
git checkout main && git pull && git checkout -b feature/<chuc-nang>

git add app/src/main/res/layout/activity_<chuc_nang>.xml
git commit -m "feat: add <chuc_nang> layout"

git add app/src/main/java/com/example/fruitshop/activities/<ChucNang>Activity.java app/src/main/AndroidManifest.xml
git commit -m "feat: add <ChucNang>Activity with list view"

git add app/src/main/java/com/example/fruitshop/activities/<ChucNang>Activity.java
git commit -m "feat: add create, update, delete for <role 1>"

git add app/src/main/java/com/example/fruitshop/activities/<ChucNang>Activity.java
git commit -m "feat: add search"

git add app/src/main/java/com/example/fruitshop/activities/<ChucNang>Activity.java
git commit -m "feat: add view-only mode for <role 2>"

git add app/src/main/java/com/example/fruitshop/activities/<ChucNang>Activity.java
git commit -m "fix: validate input before saving"

git push -u origin feature/<chuc-nang>
```

Nếu đề có nhiều màn hình (vd quản lý + đơn hàng) thì mỗi màn hình lặp lại bộ commit trên với tên Activity tương ứng.

---

## 6. Quy ước giao diện (bắt buộc, tránh lỗi bị thanh tiêu đề che)

Thẻ gốc của **mọi** layout:
```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:fitsSystemWindows="true"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:layout_margin="16dp">
```
Dùng `layout_margin` cho thẻ gốc, **không** dùng `padding` (bị `fitsSystemWindows` ghi đè).

---

## 7. Hướng dẫn cho AI

Khi được yêu cầu "làm phần của tôi theo README":
1. Hỏi (hoặc đọc) **tên người dùng** (thang / truong / hieu / khang) và **đề bài**.
2. Chỉ tạo/sửa các file trong cột "Phụ trách" của người đó ở mục 2.
3. Tạo nhánh và commit **theo đúng mẫu ở mục 5**, thay placeholder bằng tên thật theo đề, prefix `<loai>:` (KHÔNG ghi tên người trong commit message).
4. Giữ đúng package, cấu trúc thư mục, Java + SQLite + SharedPreferences.
5. Trước khi push: chạy `./gradlew assembleDebug` (nếu phần trước đã merge đủ) để chắc chắn build được.
6. Không dùng `git add .` / `git add -A`, không commit `build/`, `.idea/`, `local.properties`.
