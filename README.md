# DuAn1

**Nếu có bị Conflict thì Discard local stashed changes đi rồi fetch về từ repo**

**Layout**: Hiệp, Cao, ~~Hùng~~

**Program**: Hưng, Phi*, acay

## TODO List
### Mục khách hàng
#### Auth Activity - Đăng nhập / Đăng ký
| Task                                                      | Trạng thái    |
|-----------------------------------------------------------|---------------|
| Màn hình khởi chạy lần đầu                                | Chưa test     |
| Màn hình yêu cầu thông tin người dùng khi đăng ký lần đầu | Chưa test     |
| Đăng nhập bằng Google                                     | Hoạt động     |
| Đăng nhập bằng Facebook                                   | Hoạt động     |
| Đăng nhập bằng tài khoản thường                           | Chưa test     |
| Đăng ký bằng tài khoản thường                             | Chưa test     |
| Sử dụng tài khoản nền tảng để đăng nhập tài khoản sẵn có  | Cần check lại |
| Xác thực địa chỉ email                                    | Chưa làm      |
| Quên mật khẩu (email hoặc sđt)                            | Chưa làm      |
Còn thêm?

**Note**: Firebase không có tạo tài khoản được bằng số điện thoại nên số điện thoại chỉ được dùng làm đăng nhập hoặc quên mật khẩu

#### Màn hình chính
| Task                                                       | Trạng thái |
|------------------------------------------------------------|------------|
| Liệt kê danh sách loại sản phẩm?                           | Chưa xong  |
| Liệt kê danh sách sản phẩm nổi bật                         | Chưa xong  |
| Khung tìm kiếm sản phẩm chuyển sang danh sách sản phẩm     | Chưa làm   |
| Nhấn vào sản phẩm chuyển sang giao diện thông tin sản phẩm | Chưa làm   |
Còn thêm?

#### Màn hình tìm kiếm sản phẩm
| Task                                                     | Trạng thái |
|----------------------------------------------------------|------------|
| Liệt kê danh sách các sản phẩm                           | Chưa làm   |
| Khung tìm kiếm theo tên / loại sản phẩm                  | Chưa làm   |
| Liệt kê danh sách các sản phẩm ưa thích trong 1 tab khác | Chưa làm   |
Còn thêm?

#### Màn hình thông tin sản phẩm
| Task                           | Trạng thái |
|--------------------------------|------------|
| Hiển thị thông tin sản phẩm    | Chưa làm   |
| Nút chọn thêm vào mục ưa thích | Chưa làm   |
| Nút đặt mua sản phẩm           | Chưa làm   |
Còn thêm?

#### Màn hình giỏ hàng
| Task                                               | Trạng thái |
|----------------------------------------------------|------------|
| Liệt kê danh sách sản phẩm trong giỏ hàng hiện tại | Chưa làm   |
| Đặt mua và thanh toán (giả)                        | Chưa làm   |
Còn thêm?

#### Màn hình quản lý tài khoản
| Task                                                                    | Trạng thái |
|-------------------------------------------------------------------------|------------|
| Xem và thay đổi thông tin cá nhân                                       | Chưa làm   |
| Thay đổi ảnh đại diện (vì bth ko app nào đặt ảnh đại diện ngay lúc reg) | Chưa làm   |
| Xem những hoá đơn đã hoàn thành trước đó                                | Chưa làm   |
| Liên kết tài khoản Google / Facebook (nếu không có)                     | Chưa làm   |
| Đăng xuất                                                               | Chưa làm   |
Còn thêm?

#### Các chức năng khác sẽ có hoặc không
- Giao diện người bán
- Giao diện người giao hàng
- Live chat với người bán / người giao (phải có app người bán / người giao)
Nếu không thì nhét chung cả chức năng bán vào người dùng thông thường -> người dùng có thể bán và mua cùng lúc


## Chú ý
- Sử dụng Tiếng Anh (sớm sau gì tất cả code đều làm bằng tiếng Anh cả thôi)
- Khi đặt tên View trong layout thì theo format ``<activity>_<loại view>_<tên view>``. Ví dụ như ``login_btn_submit``. Đừng có đặt tùm lum như textView2 textView3
