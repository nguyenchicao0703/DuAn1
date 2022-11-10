# DuAn1

### Xác nhận đang hoạt động
- Đăng nhập bằng Google
- Đăng nhập bằng email/password thông qua Firebase
- Tạo tài khoản (Customer) nếu chưa có khi fast login bằng Google

### TODO List
- Loại bỏ hoàn toàn Services vì thao tác đến Firebase là async (non-blocking task)
- Sửa lại các controller phần xử lý dataSnapshot
- Thêm auth bằng Facebook SDK
- Thêm thao tác đăng ký (tương tự như đăng nhập)

### Chú ý
- Layout nên cho các view size lớn hơn
- Khi đặt tên View trong layout thì theo format <activity>_<loại view>_<tên view>
Ví dụ như ``login_btn_submit``
Đừng có đặt tùm lum như textView2 textView3
