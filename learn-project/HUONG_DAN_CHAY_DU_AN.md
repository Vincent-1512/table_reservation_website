# 🚀 HƯỚNG DẪN CHẠY DỰ ÁN RESTAURANT

---

## ⚡ QUICK START (5 PHÚT)

### **Option 1: Docker Compose (Nếu cài Docker)**

```bash
# 1. CD vào thư mục project
cd /Volumes/study/laptrinhweb/web_dat_ban

# 2. Chạy lệnh sau (sẽ build app & MySQL)
docker-compose up --build

# 3. Mở browser:
http://localhost:8080

# 4. Dừng (Ctrl+C hoặc mở terminal khác):
docker-compose down
```

**Mặc định:**
- URL: http://localhost:8080
- Database: MySQL (container)
- Username DB: root
- Password DB: root

---

### **Option 2: Local Machine (Nếu cài MySQL local)**

#### 📋 Yêu cầu:
- ✅ Java 17 trở lên
- ✅ MySQL 8.0 (chạy sẵn)
- ✅ Database "web_dat_ban" (đã tạo)

#### 🛠️ Bước 1: Kiểm tra MySQL

```bash
# Kiểm tra MySQL chạy chưa
mysql -u root -p -e "SELECT VERSION();"
```

Nếu chưa có database, tạo nó:
```bash
mysql -u root -p
```

Trong MySQL shell:
```sql
CREATE DATABASE web_dat_ban;
USE web_dat_ban;
```

#### 🛠️ Bước 2: Cập nhật Config

Mở file: `src/main/resources/application.properties`

Sửa dòng này phù hợp với MySQL của bạn:
```ini
spring.datasource.username=root       # ← Sửa username nếu cần
spring.datasource.password=root       # ← Sửa password nếu cần
```

#### 🛠️ Bước 3: Build Project

```bash
cd /Volumes/study/laptrinhweb/web_dat_ban

# Cách 1: Dùng Maven wrapper (không cần cài Maven)
./mvnw clean package -DskipTests

# Cách 2: Dùng spring-boot plugin (run trực tiếp, không tạo jar)
./mvnw spring-boot:run
```

#### 🛠️ Bước 4: Chạy Application

**Nếu dùng cách 1:**
```bash
java -jar target/restaurant-0.0.1-SNAPSHOT.jar
```

**Nếu dùng cách 2:** (đã chạy ở bước 3, bỏ qua)

#### 🛠️ Bước 5: Mở Browser

```
http://localhost:8080
```

---

## 📝 ACCOUNT TEST

Sau khi startup, admin account sẽ được tạo tự động:

### Login Mặc Định:

| Role | Username | Password | Truy cập |
|------|----------|----------|---------|
| ADMIN | admin | admin123 | /admin/menu |
| STAFF | staff | staff123 | /staff/orders |
| CUSTOMER | (đăng ký mới) | (tự chọn) | / (trang chủ) |

---

## 🐛 TROUBLESHOOTING

### ❌ Lỗi: "Connection refused to host: localhost:3306"

**Nguyên nhân:** MySQL không chạy hoặc port sai

**Giải pháp:**
```bash
# Kiểm tra MySQL status
brew services list | grep mysql

# Nếu chưa chạy, khởi động
brew services start mysql

# Hoặc kiểm tra bằng lệnh
mysql -u root -p -e "SELECT 1;"
```

### ❌ Lỗi: "Access denied for user 'root'@'localhost'"

**Nguyên nhân:** Username hoặc password sai

**Giải pháp:**
1. Mở terminal
2. Đăng nhập MySQL với password đúng:
   ```bash
   mysql -u root -p
   ```
3. Thử password lần lần cho đến khi vào được
4. Copy chính xác username & password vào `application.properties`

### ❌ Lỗi: "The goal you specified requires a project to execute but there is no POM in this directory"

**Nguyên nhân:** Chạy command từ thư mục sai

**Giải pháp:**
```bash
# ✅ Đúng: chạy từ thư mục có pom.xml
cd /Volumes/study/laptrinhweb/web_dat_ban
./mvnw clean package

# ❌ Sai: chạy từ / hoặc thư mục khác
./mvnw clean package
```

### ❌ Lỗi: "Address already in use: bind address 8080"

**Nguyên nhân:** Port 8080 đang bị chiếm

**Giải pháp:**
```bash
# Tìm process đang dùng port 8080
lsof -i :8080

# Kill process (ghi PID từ lệnh trên)
kill -9 <PID>

# Hoặc thay đổi port trong application.properties
server.port=8081
```

### ❌ Lỗi: "Java version not supported"

**Nguyên nhân:** Java < 17

**Giải pháp:**
```bash
# Kiểm tra Java version
java -version

# Cập nhật Java (macOS với Homebrew)
brew install openjdk@17
brew link openjdk@17 --force
```

---

## 🐳 DOCKER ADVANCED

### Chạy container riêng biệt

```bash
# 1. Build app image
docker build -t restaurant-app:1.0 .

# 2. Chạy MySQL container
docker run --name mysql-db \
  -e MYSQL_DATABASE=web_dat_ban \
  -e MYSQL_ROOT_PASSWORD=root \
  -p 3306:3306 \
  mysql:8.0

# 3. Chạy app container (từ terminal khác)
docker run --name restaurant-app \
  --link mysql-db \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql-db:3306/web_dat_ban \
  -p 8080:8080 \
  restaurant-app:1.0
```

### Xem logs

```bash
# Logs của app container
docker logs -f restaurant_app

# Logs của MySQL container
docker logs -f restaurant_db
```

### Cleanup Docker

```bash
# Xóa container
docker rm restaurant_app restaurant_db

# Xóa image
docker rmi restaurant-app:1.0 mysql:8.0
```

---

## 📊 KIỂM TRA STARTUP

Khi chạy thành công, bạn sẽ thấy log như này:

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot :: (v4.0.6)

2026-05-18T13:10:31.078+07:00 INFO 52393 --- [main] v.e.p.restaurant.RestaurantApplication  : Starting RestaurantApplication

2026-05-18T13:10:31.585+07:00 INFO 52393 --- [main] .s.d.r.c.RepositoryConfigurationDelegate  : Finished Spring Data repository scanning in 41 ms. Found 8 JPA repository interfaces.

2026-05-18T13:10:31.939+07:00 INFO 52393 --- [main] o.s.boot.tomcat.TomcatWebServer         : Tomcat initialized with port 8080 (http)

2026-05-18T13:10:32.140+07:00 INFO 52393 --- [main] org.hibernate.orm.core                  : HHH000001: Hibernate ORM core version 7.2.12.Final

2026-05-18T13:10:34.529+07:00 INFO 52393 --- [main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''

2026-05-18T13:10:34.534+07:00 INFO 52393 --- [main] v.e.p.restaurant.RestaurantApplication  : Started RestaurantApplication in 3.456 seconds (process running for 3.789s)
```

**✅ Nếu thấy:**
- `Tomcat started on port(s): 8080`
- `Started RestaurantApplication in ...`

**→ Ứng dụng đang chạy! Mở browser vào http://localhost:8080**

---

## 🔄 GIT WORKFLOW (NẾU CẦN)

```bash
# Clone project
git clone <repo-url>
cd web_dat_ban

# Cập nhật từ remote
git pull origin main

# Tạo branch mới để develop
git checkout -b feature/your-feature

# Commit changes
git add .
git commit -m "Add feature xyz"

# Push lên
git push origin feature/your-feature
```

---

## 🎯 TIPS & TRICKS

### 1. Hot Reload (tự reload khi code thay đổi)

Thêm dependency vào pom.xml:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

Chạy:
```bash
./mvnw spring-boot:run
```

Giờ khi thay đổi file → tự động reload (nhanh hơn restart full)

### 2. Debug Mode

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--debug"
```

Hoặc IDE (Eclipse): Run → Debug As → Spring Boot App

### 3. Profile khác nhau

```bash
# Chạy với profile docker
java -jar target/restaurant-0.0.1-SNAPSHOT.jar --spring.profiles.active=docker

# Chạy với profile local
java -jar target/restaurant-0.0.1-SNAPSHOT.jar --spring.profiles.active=local

# Config sẽ load từ application-{profile}.properties
```

### 4. View SQL logs

Thêm vào application.properties:
```ini
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
```

### 5. Skip test khi build

```bash
./mvnw clean package -DskipTests
```

### 6. Build offline (nếu internet tắt)

```bash
./mvnw clean package -o -DskipTests
```

---

## 📖 THÊM TÀI LIỆU

Xem file `PHAN_TICH_DU_AN_CHI_TIET.md` để hiểu:
- Kiến trúc project
- Luồng request
- Entity relationships
- Security configuration
- Câu hỏi vấn đáp phổ biến

---

## ✅ CHECKLIST TRƯỚC VĂN ĐÁP

- [ ] Đã chạy được project (local hoặc Docker)
- [ ] Đã login được với account test
- [ ] Đã test các tính năng cơ bản:
  - [ ] Xem menu
  - [ ] Thêm vào giỏ hàng
  - [ ] Đặt bàn
  - [ ] Admin quản lý menu
  - [ ] Admin quản lý bàn
  - [ ] Staff quản lý đơn hàng
- [ ] Đã đọc `PHAN_TICH_DU_AN_CHI_TIET.md`
- [ ] Đã biết trả lời câu hỏi trong phần "Câu hỏi ôn vấn đáp"
- [ ] Đã hiểu Spring Security flow
- [ ] Đã hiểu Entity relationships & DB schema

---

**Happy coding! 🎉**
