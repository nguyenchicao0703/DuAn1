# DuAn1

**Nếu có bị Conflict thì Discard local stashed changes đi rồi fetch về từ repo**

### TODO List
:x: Thêm thao tác đăng ký (tương tự như đăng nhập)

:x: Làm các layout khác đi đm

:x: Liên kết tài khoản Facebook, Google với tài khoản password

:x: Sửa lại toàn bộ controller sang sử dụng ``addOnCompleteListener(task -> task.isSuccessful()...)`` vì success/failure listener có khi không trigger

:x: TBA..

:white_check_mark: Đăng nhập bằng Google

:white_check_mark: Đăng nhập bằng email/password thông qua Firebase**

:white_check_mark: Đăng nhập bằng Facebook

:white_check_mark: Tạo tài khoản (Customer) nếu chưa có khi fast login bằng Google/Facebook

:white_check_mark: Đăng nhập vào tài khoản có sẵn trùng email bằng nền tảng khác (reg = Google, log = Facebook)

### Chú ý
- Ghi ra những thay đổi trong commit message để biết cái gì đã xong hoặc chưa xong
- Layout nên cho các view size lớn hơn
- Khi đặt tên View trong layout thì theo format ``<activity>_<loại view>_<tên view>``. Ví dụ như ``login_btn_submit``. Đừng có đặt tùm lum như textView2 textView3
