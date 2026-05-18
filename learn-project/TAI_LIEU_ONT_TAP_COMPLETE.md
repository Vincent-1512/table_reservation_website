# 📚 TẤT CẢ TÀI LIỆU ÔN TẬP - HƯỚNG DẪN SỬ DỤNG

---

## 🎯 5 FILES TÀI LIỆU ĐƯỢC TẠO

Tôi đã tạo **5 file Markdown chi tiết** trong thư mục dự án:

```
/Volumes/study/laptrinhweb/web_dat_ban/
├── README_ONT_TAP.md                      ← Bạn đang đọc file này
├── PHAN_TICH_DU_AN_CHI_TIET.md           ← CHÍNH - Full analysis (100KB+)
├── HUONG_DAN_CHAY_DU_AN.md               ← Hướng dẫn chạy + Troubleshoot
├── CHEAT_SHEET.md                        ← 1 trang tómo tắt (print & mang)
├── DATABASE_SCHEMA.md                    ← Schema & Entity relations
└── README.md                             ← (File gốc của project)
```

---

## 📖 HƯỚNG DẪN ĐỌC TỪNG FILE

### **1. 📋 PHAN_TICH_DU_AN_CHI_TIET.md** ⭐⭐⭐ (MUST READ)

**Kích thước:** ~30 trang in (100KB)

**Nội dung:**
- I. Giới thiệu dự án
- II. Cấu trúc thư mục
- III. Luồng hoạt động (startup, auth, login, features)
- IV. Luồng chính các tính năng (menu, cart, reservation, order, report)
- V. Entity & DB relationships
- VI. Spring Security configuration
- VII. CartService (@SessionScope)
- VIII. Cách chạy (Docker + Local)
- IX. Files quan trọng
- X. Những điểm cần nhớ
- XI. **10 Câu hỏi vấn đáp + Trả lời chi tiết**
- XII. Tómo tắt
- XIII. Checklist ôn tập

**Cách dùng:**
```
1. Ngày đầu: Đọc nhanh Part I-II (30 phút)
2. Ngày 2-3: Đọc kỹ Part III-VII (2-3 tiếng)
3. Ngày 3-4: Đọc Part VIII-XI (tập trả lời 10 câu hỏi)
4. Ngày 5: Ôn lại Part X-XIII
```

**Tip:** In ra + highlight những điểm quan trọng!

---

### **2. 🚀 HUONG_DAN_CHAY_DU_AN.md** ⭐⭐ (PRACTICAL)

**Kích thước:** ~15 trang in (40KB)

**Nội dung:**
- Quick Start (5 phút) với Docker Compose
- Local setup (MySQL + Java)
- Step-by-step hướng dẫn
- Default test accounts
- Troubleshooting lỗi phổ biến:
  - Connection refused
  - Access denied
  - Port already in use
  - Java version error
- Docker advanced commands
- Tips & Tricks (Hot reload, Debug, Profiles)
- Checklist trước thi

**Cách dùng:**
```
1. Chọn Option 1 (Docker) hoặc Option 2 (Local)
2. Thực hiện step-by-step
3. Nếu gặp lỗi → xem Troubleshooting section
4. Khi chạy success → test tất cả features
```

**Tip:** Nên chạy docker-compose lần đầu từ sớm!

---

### **3. 📌 CHEAT_SHEET.md** ⭐⭐⭐ (PRINT & BRING)

**Kích thước:** 1 trang in (2KB)

**Nội dung:**
- Tómo tắt project (1 table)
- Cấu trúc folder (ASCII)
- 8 Entities & relationships
- Spring Security rules
- Login flow (ASCII diagram)
- CartService giải thích
- Key files
- Chạy project (2 cách)
- Default accounts
- **10 Câu hỏi + Trả lời ngắn**
- Endpoints
- Checklist

**Cách dùng:**
```
1. ⭐ IN RA (1 trang duy nhất!)
2. Mang theo khi thi vấn đáp
3. Nếu thầy hỏi gì → nhìn vào sheet
4. Ngoài ra, dựa vào sự chuẩn bị + kiến thức
```

**Tip:** Gấp lại vào túi quần, hoặc ghi vào notebook!

---

### **4. 💾 DATABASE_SCHEMA.md** ⭐ (DEEP UNDERSTANDING)

**Kích thước:** ~20 trang in (50KB)

**Nội dung:**
- ER Diagram (ASCII art)
- SQL schema cho 8 bảng
- Detailed descriptions cho mỗi table
- Relationships summary
- Java Entity annotations
- Indexes
- Enums (Role, OrderStatus...)
- Data flow example
- Optimistic locking example
- Query examples

**Cách dùng:**
```
1. Đọc kỹ phần ER Diagram
2. Hiểu từng relationship
3. Vẽ lại diagram từ memory
4. Tự viết SQL queries
5. Explain data flow khi user đặt bàn
```

**Tip:** Database = 70% của project, hiểu kỹ!

---

### **5. 📚 README_ONT_TAP.md** (OVERVIEW - BẠN ĐANG ĐỌC)

**Kích thước:** ~5 trang in (15KB)

**Nội dung:**
- Summary tất cả tài liệu
- Roadmap ôn tập 3-5 ngày
- Top 10 câu hỏi + trả lời
- Technical details
- File mapping
- Phần trị có thể hỏi
- Final tips
- Quick reference

**Cách dùng:**
```
1. Đây là "lộ trình ôn tập"
2. Follow roadmap day by day
3. Dùng như checklist
```

---

## 🗓️ LỘ TRÌNH ÔN TẬP (3-5 NGÀY)

### **Ngày 1 (2-3 giờ): BUILD & RUN**

```
Step 1: Đọc HUONG_DAN_CHAY_DU_AN.md (10 phút)
Step 2: Chạy docker-compose up --build (5 phút)
Step 3: Mở http://localhost:8080 (1 phút)
Step 4: Login admin/admin123 (2 phút)
Step 5: Thử tất cả tính năng (30 phút):
   ✓ Xem menu
   ✓ Thêm giỏ hàng
   ✓ Xem giỏ hàng
   ✓ Đặt bàn
   ✓ Admin quản lý menu (thêm, sửa, xóa category)
   ✓ Admin quản lý bàn
   ✓ Staff xem đơn hàng
   ✓ Logout
Step 6: Đọc PHAN_TICH_DU_AN_CHI_TIET.md Part I-II (1 giờ)
```

---

### **Ngày 2 (3-4 giờ): KIẾN TRÚC & WORKFLOW**

```
Step 1: Đọc PHAN_TICH_DU_AN_CHI_TIET.md Part III-IV (1.5 giờ)
        - Luồng hoạt động
        - Authentication & Authorization flow
        - Tính năng chính

Step 2: Đọc DATABASE_SCHEMA.md (1.5 giờ)
        - ER Diagram
        - 8 Entities
        - Relationships
        - Optimistic Locking

Step 3: Vẽ lại ER diagram từ memory (30 phút)

Step 4: Nằm mộng trong giấc ngủ 😴 (haha)
```

---

### **Ngày 3-4 (3-4 giờ): SECURITY & CODE**

```
Step 1: Đọc PHAN_TICH_DU_AN_CHI_TIET.md Part V-VII (1.5 giờ)
        - Spring Security config
        - CartService (@SessionScope)
        - Key files

Step 2: Mở Eclipse, đọc code:
        ✓ RestaurantApplication.java (5 phút)
        ✓ SecurityConfig.java (10 phút)
        ✓ AuthController.java (10 phút)
        ✓ CustomerController.java (15 phút)
        ✓ AdminMenuController.java (10 phút)
        ✓ CartService.java (10 phút)
        ✓ User.java, Order.java, MenuItem.java (15 phút)
        ✓ UserService, OrderService (15 phút)

Step 3: Trace flow trong code (1 giờ)
        - Khi user login → code nào xử lý?
        - Khi user thêm cart → CartService làm gì?
        - Khi user đặt bàn → data đi đâu?
        - Khi staff confirm order → table status thay đổi sao?

Step 4: Modify code & test (30 phút)
        - Thêm 1 field vào MenuItem
        - Update form HTML
        - Run & verify no error
```

---

### **Ngày 5 (2-3 giờ): MOCK INTERVIEW**

```
Step 1: Ôn lại CHEAT_SHEET.md (20 phút)

Step 2: Tự mock interview (1.5-2 giờ)
        Câu hỏi:
        Q1: Giới thiệu dự án?
        Q2: Tại sao dùng Spring Boot?
        Q3: Database có mấy bảng, relationship nào?
        Q4: Spring Security hoạt động sao?
        Q5: Khi 2 customer đặt cùng bàn?
        Q6: CartService dùng @SessionScope tại sao?
        Q7: ADMIN/STAFF/CUSTOMER có quyền gì?
        Q8: Password mã hóa sao?
        Q9: Thêm role mới làm sao?
        Q10: Xuất báo cáo Excel/PDF?

        Trả lời tự do (không nhìn tài liệu)
        → Kiểm tra câu trả lời có đúng
        → Nếu sai → đọc lại
        → Lặp lại cho đến pass all

Step 3: Chuẩn bị tâm lý (30 phút)
        ✓ Tự tin vào sự chuẩn bị
        ✓ Nếu bị hỏi khó → không sợ, thật thà nói "chưa học"
        ✓ Ghi chép ý chính thay vì viết cả đoạn
```

---

## 📋 DANH SÁCH 10 CÂUHỎI PHỔ BIẾN

### **Q1: Dự án của bạn là gì?**
📌 Xem CHEAT_SHEET.md (Row "Tên")
📌 Xem PHAN_TICH_DU_AN_CHI_TIET.md (Part I)

### **Q2: Công nghệ sử dụng?**
📌 Xem PHAN_TICH_DU_AN_CHI_TIET.md (Bảng công nghệ)

### **Q3: Database schema?**
📌 Xem DATABASE_SCHEMA.md (ER Diagram)

### **Q4: Spring Security hoạt động sao?**
📌 Xem PHAN_TICH_DU_AN_CHI_TIET.md (Part VI)

### **Q5: Khi 2 người đặt cùng bàn?**
📌 Xem DATABASE_SCHEMA.md (Optimistic Locking)

### **Q6: CartService?**
📌 Xem PHAN_TICH_DU_AN_CHI_TIET.md (Part VII)

### **Q7: Vai trò (ADMIN/STAFF/CUSTOMER)?**
📌 Xem README_ONT_TAP.md (Q7)

### **Q8: Password mã hóa?**
📌 Xem README_ONT_TAP.md (Q8)

### **Q9: Thêm role mới?**
📌 Xem README_ONT_TAP.md (Q9)

### **Q10: Xuất Excel/PDF?**
📌 Xem README_ONT_TAP.md (Q10)

---

## 🎯 QUICK CHECKLIST

### Trước khi vấn đáp:

```
Ngày trước:
  ☐ Đọc lại CHEAT_SHEET.md (1 trang)
  ☐ Mock interview 10 câu hỏi
  ☐ Ngủ đủ giấc (8 tiếng)

Sáng thi:
  ☐ Ăn cơm/sáng đủ
  ☐ Mặc gọn gàng
  ☐ Mang CHEAT_SHEET.md (nếu được)
  ☐ Mang notebook + bút

Tại phòng thi:
  ☐ Nghe kỹ đề hỏi (đừng vội trả lời)
  ☐ Trả lời rõ ràng, chi tiết
  ☐ Vẽ diagram nếu cần
  ☐ Không sợ khi bị hỏi khó (thật thà nói không biết)
  ☐ Hỏi ngược thầy nếu thắc mắc
```

---

## 🔗 FILE LOCATIONS

```
Main project folder:
/Volumes/study/laptrinhweb/web_dat_ban/

Documentation files:
├── README_ONT_TAP.md (this file)
├── PHAN_TICH_DU_AN_CHI_TIET.md ⭐⭐⭐
├── HUONG_DAN_CHAY_DU_AN.md ⭐⭐
├── CHEAT_SHEET.md ⭐⭐⭐ (PRINT THIS!)
└── DATABASE_SCHEMA.md ⭐

Source code:
├── src/main/java/vn/edu/ptit/restaurant/
│   ├── controller/ (7 controllers)
│   ├── service/ (13 services)
│   ├── repository/ (8 repositories)
│   ├── entity/ (8 entities)
│   ├── security/ (3 security classes)
│   └── dto/ (2 DTOs)
├── src/main/resources/
│   ├── application.properties (local config)
│   ├── application-docker.properties (docker config)
│   ├── templates/ (Thymeleaf HTML)
│   └── static/ (CSS, JS, images)
└── pom.xml (dependencies)

Build artifacts:
├── target/
│   ├── restaurant-0.0.1-SNAPSHOT.jar (runnable jar)
│   └── classes/ (compiled classes)
├── docker-compose.yml (Docker setup)
└── Dockerfile (Docker image)
```

---

## 💡 FINAL TIPS

1. **Print CHEAT_SHEET.md** - 1 trang duy nhất, mang theo
2. **Hiểu database trước** - Database = 70% project
3. **Vẽ diagram** - Giải thích tốt hơn khi vẽ ER diagram
4. **Chạy thực tế** - Docker-compose lần 1 từ sớm
5. **Code reading** - Đọc code trong IDE (Eclipse)
6. **Trace flow** - Follow request từ controller → service → DB
7. **Tự tin** - Bạn đã chuẩn bị kỹ!
8. **Thật thà** - Nếu không biết → nói "chưa học phần này"
9. **Mock interview** - Lặp lại 10 câu hỏi nhiều lần
10. **Ngủ đủ** - Ngủ 8 tiếng đêm trước thi!

---

## 📞 QUICK COMMANDS

```bash
# Start project
docker-compose up --build

# Or local
./mvnw spring-boot:run

# Access
http://localhost:8080

# Default accounts
admin / admin123
staff / staff123
```

---

## 🎊 READY?

**Tất cả tài liệu đã được chuẩn bị chi tiết. Bạn có thể:**

1. ✅ Chạy project thành công
2. ✅ Hiểu kiến trúc từ A-Z
3. ✅ Trả lời tất cả 10 câu hỏi phổ biến
4. ✅ Vẽ được ER diagram
5. ✅ Giải thích Spring Security flow
6. ✅ Trace code request
7. ✅ Sẵn sàng vấn đáp! 💪

---

**LỘ TRÌNH ÔN TẬP: HOÀN TẤT ✅**

**THỜI GIAN BỘ CHUẨN BỊ: 3-5 NGÀY**

**KÊTQUẢ MONG MUỐN: ĐỖ VĂN ĐÁP 🎓**

---

**CHÚC BẠN THI TỐT! 🍀🎉**

*Chuẩn bị bởi: GitHub Copilot*
*Ngày: 18/05/2026*
*Workspace: PTIT Restaurant Project*
