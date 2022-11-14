# DuAn1

**Nếu có bị Conflict thì Discard local stashed changes đi rồi fetch về từ repo**

### TODO List

Ghi chú ``[<1>:<2>:<3>]``
```yaml
<1>:
  A: "Mục Khách hàng"
  B: "Mục Người bán"
  C: "Mục người giao"
  
<2>:
  L: "Layout / Giao diện"
  F: "Function / Chức năng (code)"
  
<3>: "Tên của cụm chức năng"
```

:x: Làm các layout khác đi đm

:x: ``[A:L/F:Auth]`` Trang khởi chạy ứng dụng lần đầu (Giới thiệu app)

:x: ``[A:L/F:Auth]`` Trang yêu cầu thêm thông tin người dùng khi đăng nhập lần đầu

:x: ``[A:F:Account Settings Activity]`` Liên kết tài khoản Facebook, Google với tài khoản password trong mục quản lý tài khoản

:x: ``[A:L/F:Home Activity]`` Trang chủ hiển thị sản phẩm nổi bật, các loại sản phẩm, tba..

:x: ``[A:L/F:Account Settings Activity]`` Trang quản lý thông tin tài khoản

:x: ``[A:L/F:Order History Activity]`` Trang quản lý các đơn hàng / lịch sử mua

:x: ``[A:L/F:Search Product Activity]`` Trang tìm kiếm sản phẩm

:x: ``[A:L/F:Item Info Activity]`` Trang thông tin của 1 sản phẩm / người hoặc nhà bán

:x: ``[B:L/F:ALL]`` Trang và chức năng của người / nhà bán

:x: ``[C:L/F:ALL]`` Trang và chức năng của người giao hàng (maybe)

:x: TBA..

:white_check_mark: ``[A:L:Auth]`` Layout đăng ký / đăng nhập tài khoản

:white_check_mark: ``[A:F:Auth]`` Đăng nhập bằng Google

:white_check_mark: ``[A:F:Auth]`` Đăng nhập bằng Facebook

:white_check_mark: ``[A:F:Auth]`` Đăng nhập / đăng ký bằng email / password thông qua Firebase**

:white_check_mark: ``[A:F:Auth]`` Tạo tài khoản (Customer) nếu chưa có khi fast login bằng Google/Facebook

:white_check_mark: ``[A:F:Auth]`` Đăng nhập vào tài khoản có sẵn trùng email bằng nền tảng khác (reg = Google, log = Facebook)

### Chú ý
- Ghi ra những thay đổi trong commit message để biết cái gì đã xong hoặc chưa xong
- Layout nên cho các view size lớn hơn
- Khi đặt tên View trong layout thì theo format ``<activity>_<loại view>_<tên view>``. Ví dụ như ``login_btn_submit``. Đừng có đặt tùm lum như textView2 textView3
