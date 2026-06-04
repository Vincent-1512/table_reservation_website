# Edge Case Phan Customer

Tai lieu nay tong hop cac truong hop bien, loi co the gap va cac validate da trien khai trong luong khach hang cua du an dat ban nha hang.

## 1. Luong Dat Ban

| STT | Edge case | Ket qua mong doi |
| --- | --- | --- |
| 1 | Khach chua dang nhap nhung truy cap `/reservation`, `/cart`, `/my-reservations` | Chuyen ve trang dang nhap |
| 2 | Khach chon ngay dat ban trong qua khu | Khong cho dat ban, hien thong bao loi |
| 3 | Khach chon ngay dat ban vuot qua gioi han 14 ngay | Khong cho dat ban, hien thong bao chi nhan dat truoc toi da 14 ngay |
| 4 | Khach nhap so luong khach bang `0` hoac so am | Khong cho submit form |
| 5 | Khach nhap so luong khach qua lon | Chi hien ban co suc chua phu hop hoac bao khong co ban phu hop |
| 6 | Khach khong chon ban nhung van bam xac nhan | Chan submit va yeu cau chon ban |
| 7 | Ban hien thi la trong nhung da bi nguoi khac dat truoc khi submit | Server tu choi tao dat ban va thong bao ban khong con trong |
| 8 | Ban duoc chon co suc chua nho hon so khach | Tu choi dat ban |
| 9 | Ban co trang thai `RESERVED`, `OCCUPIED` hoac `MAINTENANCE` | Khong cho chon/dat ban |
| 10 | Nguoi dung sua request gui `tableId` khong ton tai | Server tra loi va hien thong bao loi |
| 11 | Nguoi dung sua request gui `reservationDate` hoac `reservationTime` sai dinh dang | Server bat loi parse du lieu va thong bao loi |
| 12 | Khach reload lai trang success/thanh toan nhieu lan | Khong tao them reservation moi |
| 13 | Khach truy cap `/reservation/success?id=...` cua nguoi khac | Redirect ve `/my-reservations` |
| 14 | Khach huy reservation khong thuoc tai khoan cua minh | Tu choi huy va bao khong co quyen |
| 15 | Khach huy reservation da bi huy hoac da hoan thanh | Khong nen cho huy lai |
| 16 | Khach huy reservation co order mon an di kem | Reservation chuyen `CANCELLED`, ban ve `AVAILABLE`, order lien quan chuyen `CANCELLED` |

## 2. Luong Gio Hang Va Dat Mon

| STT | Edge case | Ket qua mong doi |
| --- | --- | --- |
| 1 | Khach them mon khong ton tai bang cach sua `menuItemId` | Server tu choi, khong them vao gio |
| 2 | Khach them mon da bi admin tat trang thai kha dung | Khong cho them mon |
| 3 | Khach gui so luong mon bang `0`, so am hoac qua lon | Tu choi hoac gioi han so luong hop le |
| 4 | Khach cap nhat gio hang voi `menuItemId` khong co trong gio | Khong thay doi gio hang, co the hien thong bao loi |
| 5 | Khach bam "Hoan tat chon mon" khi gio hang rong | Redirect ve menu va thong bao can chon it nhat mot mon |
| 6 | Session het han lam mat gio hang | Gio hang rong, nguoi dung can chon lai mon |
| 7 | Khach dat ban xong roi quay lai chon mon voi `reservationId` khong ton tai | Server tu choi va thong bao loi |
| 8 | Khach chon mon cho reservation khong thuoc tai khoan cua minh | Tu choi thao tac |
| 9 | Khach chon mon cho reservation da huy hoac da hoan thanh | Khong cho tao/cap nhat order |

## 3. Luong Thanh Toan

| STT | Edge case | Ket qua mong doi |
| --- | --- | --- |
| 1 | Khach vao trang thanh toan voi `reservationId` khong ton tai | Bao loi hoac redirect ve danh sach dat ban |
| 2 | Khach vao trang thanh toan cua nguoi khac | Redirect ve `/my-reservations` |
| 3 | Khach thanh toan lai reservation da `CONFIRMED` | Khong xu ly lai giao dich, thong bao trang thai khong hop le |
| 4 | Khach thanh toan mon an nhung order chua duoc tao | Redirect ve menu de chon mon |
| 5 | Khach gui `paymentMode` khong hop le | Xu ly theo mac dinh an toan hoac bao loi |
| 6 | Thanh toan gia lap thanh cong nhung server loi khi cap nhat trang thai | Rollback transaction va thong bao thanh toan that bai |

## 4. Luong Ho So Ca Nhan

| STT | Edge case | Ket qua mong doi |
| --- | --- | --- |
| 1 | Ho ten de trong hoac chi gom khoang trang | Tu choi cap nhat |
| 2 | So dien thoai khong dung dinh dang 9-11 chu so | Tu choi cap nhat |
| 3 | Email sai dinh dang | Tu choi cap nhat |
| 4 | Mat khau hien tai nhap sai | Khong cho doi mat khau |
| 5 | Mat khau moi duoi 6 ky tu | Tu choi doi mat khau |
| 6 | Mat khau xac nhan khong khop | Tu choi doi mat khau |
| 7 | Upload avatar khong phai file anh | Tu choi upload |
| 8 | Upload file anh qua lon | Tu choi upload theo cau hinh multipart |

## 5. Validate Du Lieu Dau Vao Da Trien Khai

### 5.1. Dat Ban

| Noi validate | Du lieu validate | Cach validate |
| --- | --- | --- |
| `reservation.html` | `tableId` | `required` va JavaScript chan submit neu chua chon ban |
| `reservation.html` | `reservationDate` | `required` |
| `reservation.html` | `reservationTime` | `required` |
| `reservation.html` | `numberOfGuests` | `required`, `min="1"` |
| `reservation.html` | Ngay dat ban | JavaScript khong cho chon ngay nho hon ngay hien tai |
| `reservation.html` | Gioi han ngay dat ban | JavaScript khong cho chon qua 14 ngay |
| `reservation.html` | Suc chua ban | JavaScript loc danh sach ban theo so luong khach |
| `ReservationRequest` | `tableId` | `@NotNull` |
| `ReservationRequest` | `reservationDate` | `@NotBlank` |
| `ReservationRequest` | `reservationTime` | `@NotBlank` |
| `ReservationRequest` | `numberOfGuests` | `@NotNull`, `@Min(1)` |
| `CustomerController.submitReservation()` | Du lieu form dat ban | `@Valid @ModelAttribute ReservationRequest` va `BindingResult` |

### 5.2. Gio Hang Va Dat Mon

| Noi validate | Du lieu validate | Cach validate |
| --- | --- | --- |
| `cart.html` | `quantity` | Input so luong mon co `min="1"` |
| `CartValidator.validateNotEmpty()` | Gio hang rong | Kiem tra `cartService.getItems().isEmpty()` |
| `ReservationServiceImpl.createOrderForReservation()` | Tao order cho reservation | Goi `CartValidator.validateNotEmpty()` truoc khi tao order |
| `CustomerController.showFoodPaymentPage()` | Order cua reservation chua ton tai | Kiem tra `orderOpt.isEmpty()` va redirect ve menu de chon mon |

### 5.3. Ho So Ca Nhan

| Noi validate | Du lieu validate | Cach validate |
| --- | --- | --- |
| `profile.html` | `fullName` | `required` |
| `profile.html` | `phone` | `required`, `pattern="[0-9]{9,11}"` |
| `profile.html` | `email` | `required`, `type="email"` |
| `profile.html` | Ho ten rong | JavaScript `validateProfileForm()` chan submit |
| `profile.html` | So dien thoai sai dinh dang | JavaScript regex `^[0-9]{9,11}$` |
| `ProfileUpdateRequest` | Ho ten rong | `@NotBlank` |
| `ProfileUpdateRequest` | So dien thoai rong/sai dinh dang | `@NotBlank`, `@Pattern(regexp = "^[0-9]{9,11}$")` |
| `ProfileUpdateRequest` | Email rong/sai dinh dang | `@NotBlank`, `@Email` |
| `CustomerController.updateProfile()` | Du lieu cap nhat ho so | `@Valid @ModelAttribute ProfileUpdateRequest` va `BindingResult` |

### 5.4. Doi Mat Khau

| Noi validate | Du lieu validate | Cach validate |
| --- | --- | --- |
| `profile.html` | `currentPassword` | `required` |
| `profile.html` | `newPassword` | `required` |
| `profile.html` | `confirmPassword` | `required` |
| `profile.html` | Do dai mat khau moi | JavaScript chan neu duoi 6 ky tu |
| `profile.html` | Xac nhan mat khau | JavaScript chan neu khong khop |
| `ChangePasswordRequest` | Mat khau hien tai | `@NotBlank` |
| `ChangePasswordRequest` | Mat khau moi | `@NotBlank`, `@Size(min = 6)` |
| `ChangePasswordRequest` | Xac nhan mat khau | `@NotBlank` |
| `CustomerController.changePassword()` | Du lieu doi mat khau | `@Valid @ModelAttribute ChangePasswordRequest` va `BindingResult` |
| `CustomerProfileValidator.validatePasswordChange()` | Mat khau hien tai | Kiem tra `passwordEncoder.matches()` |
| `CustomerProfileValidator.validatePasswordChange()` | Xac nhan mat khau | Kiem tra `newPassword.equals(confirmPassword)` |

### 5.5. Upload Avatar

| Noi validate | Du lieu validate | Cach validate |
| --- | --- | --- |
| `profile.html` | File upload avatar | Input file co `accept="image/*"` |
| `CustomerProfileValidator.validateAvatar()` | File rong | Kiem tra `file.isEmpty()` |
| `CustomerProfileValidator.validateAvatar()` | Dinh dang file | Kiem tra `contentType != null` va `contentType.startsWith("image/")` |
| `application.properties` | Dung luong file upload | `spring.servlet.multipart.max-file-size=5MB`, `spring.servlet.multipart.max-request-size=5MB` |

## 6. Validate Nghiep Vu Da Trien Khai

| STT | Nghiep vu duoc validate | Noi xu ly | Cach validate/ket qua |
| --- | --- | --- | --- |
| 1 | Khach phai dang nhap moi duoc dat ban | `CustomerController` | Kiem tra `principal == null`, neu chua dang nhap thi redirect ve `/login` |
| 2 | Ngay dat ban khong duoc nam trong qua khu | `ReservationValidator.validateBookingDate()` | Kiem tra ngay dat ban khong nho hon ngay hien tai |
| 3 | Chi nhan dat ban truoc toi da 14 ngay | `ReservationValidator.validateBookingDate()` | Kiem tra ngay dat ban khong vuot qua `today.plusDays(14)` |
| 4 | Ban phai con trong moi duoc dat | `ReservationValidator.validateTableAvailable()` | Kiem tra `table.status == AVAILABLE` |
| 5 | Ban phai du suc chua cho so luong khach | `ReservationValidator.validateTableAvailable()` | Kiem tra `table.capacity >= numberOfGuests` |
| 6 | Service goi validator truoc khi tao reservation | `ReservationServiceImpl.createReservation()` | Goi `validateBookingDate()` va `validateTableAvailable()` |
| 7 | Khong cho dat trung khung gio | `ReservationServiceImpl.createReservation()` | Kiem tra `countOverlappingReservations()` trong khoang truoc/sau 2 tieng |
| 8 | Reservation moi co trang thai cho xu ly | `ReservationServiceImpl.createReservation()` | Tao `Reservation` voi trang thai `PENDING` |
| 9 | Gio hang co mon thi tao order dat mon truoc | `ReservationServiceImpl.createReservation()` | Neu `cartService.getCount() > 0` thi tao `Order`, `OrderItem`, sau do xoa gio hang |
| 10 | Khong cho hoan tat chon mon khi gio hang rong | `CartValidator.validateNotEmpty()` | Neu gio hang rong thi throw loi |
| 11 | Khach chi duoc xem reservation cua minh | `ReservationValidator.validateOwner()` va `ReservationServiceImpl.findByIdForUser()` | So sanh user cua reservation voi username hien tai |
| 12 | Khach chi duoc thanh toan reservation cua minh | `ReservationServiceImpl.processDepositPayment()` va `ReservationServiceImpl.processFoodPayment()` | Goi `findByIdForUser()` truoc khi xu ly |
| 13 | Khong cho thanh toan mon an khi order chua ton tai | `CustomerController.showFoodPaymentPage()` | Neu order khong ton tai thi redirect ve menu chon mon |
| 14 | Thanh toan coc thanh cong thi xac nhan reservation | `ReservationServiceImpl.processDepositPayment()` | Chuyen reservation tu `PENDING` sang `CONFIRMED` |
| 15 | Thanh toan mon thanh cong thi xac nhan reservation va order | `ReservationServiceImpl.processFoodPayment()` | Chuyen reservation sang `CONFIRMED`; neu `paymentMode=full` thi order sang `CONFIRMED` va set `amountPaid` |
| 16 | Chi xac nhan reservation dang `PENDING` | `ReservationValidator.validatePending()` | Neu reservation khong phai `PENDING` thi throw loi |
| 17 | Khach khong duoc huy reservation cua nguoi khac | `ReservationServiceImpl.cancelReservation()` | Kiem tra `res.getUser().getId().equals(userId)` |
| 18 | Huy reservation phai tra ban ve trang thai trong | `ReservationServiceImpl.cancelReservation()` | Cap nhat `Reservation.status = CANCELLED`, `DiningTable.status = AVAILABLE` |
| 19 | Huy reservation phai huy order lien quan neu co | `ReservationServiceImpl.cancelReservation()` | Neu co order lien quan thi cap nhat `Order.status = CANCELLED` |

## 7. Ghi Chu Kiem Thu

- Client-side validation giup chan loi som tren giao dien, nhung khong du de bao ve he thong.
- Server-side validation van bat buoc vi nguoi dung co the sua request truc tiep.
- Controller hien chu yeu dieu phoi request/response, doc `BindingResult`, goi service va redirect.
- Input validation nam trong DTO request bang annotation.
- Business validation nam trong `validator/customer` va duoc service goi lai de tranh bo qua khi request bi sua thu cong.
- Cac case lien quan den dat ban, tao order va thanh toan nen duoc boc trong transaction de tranh du lieu luu do dang.
