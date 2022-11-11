# DuAn1

**Nếu có bị Conflict thì Discard local stashed changes đi rồi fetch về từ repo**

### TODO List
[-] Thêm thao tác đăng ký (tương tự như đăng nhập)

[-] Làm các layout khác đi đm

[-] Liên kết tài khoản Facebook, Google với tài khoản password

[-] Sửa lại toàn bộ controller sang sử dụng ``addOnCompleteListener(task -> task.isSuccessful()...)`` vì success/failure listener có khi không trigger

[-] TBA..

[+] Đăng nhập bằng Google

[+] Đăng nhập bằng email/password thông qua Firebase**

[+] Đăng nhập bằng Facebook

[+] Tạo tài khoản (Customer) nếu chưa có khi fast login bằng Google/Facebook

[+] Đăng nhập vào tài khoản có sẵn trùng email bằng nền tảng khác (reg = Google, log = Facebook)

### Chú ý
- Ghi ra những thay đổi trong commit message để biết cái gì đã xong hoặc chưa xong
- Layout nên cho các view size lớn hơn
- Khi đặt tên View trong layout thì theo format ``<activity>_<loại view>_<tên view>``. Ví dụ như ``login_btn_submit``. Đừng có đặt tùm lum như textView2 textView3
