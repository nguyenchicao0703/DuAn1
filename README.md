# Cheetah (not fast) Food Delivery
Vì thiếu nhân lực, thời gian và quản lý đội ngũ thì app bị giới hạn rất nhiều so với dự tính ban đầu

### Note:
- ~~Google doesn't like hanging UI thread (main thread) so everything has to be asynchronous~~
- Need to add check for account details in ``AuthLogin`` since users can skip fill bio 
activity in ``AuthRegister``

#### Nhóm 6 (lần 2) - Tổng nghỉ: 4

**Layout**: Chí Cao

**Program**: acay

**Presentation**: Hưng, Phi

~~Rút: Hùng, Hiệp, 2 thằng nữa không biết~~I

## TODO List
### Mục khách hàng
#### Auth Activity - Đăng nhập / Đăng ký
| Task                                                      | Trạng thái                          |
|-----------------------------------------------------------|-------------------------------------|
| Layout đăng nhập / đăng ký                                | Complete                            |
| Layout điền thông tin chi tiết sau đăng ký                | Complete                            |
| Layout splash loading screen                              | Complete                            |
| Layout chạy app lần đầu                                   | Sửa lại                             |
| Màn hình khởi chạy lần đầu                                | Working                             |
| Màn hình yêu cầu thông tin người dùng khi đăng ký lần đầu | Working (assumption)                |
| Đăng nhập bằng Google                                     | Working                             |
| Đăng nhập bằng Facebook                                   | Not working (Firebase setup issues) |
| Đăng nhập bằng tài khoản thường                           | Working (assumption)                |
| Đăng ký bằng tài khoản thường                             | Working (assumption)                |
| Sử dụng tài khoản nền tảng để đăng nhập tài khoản sẵn có  | Working (assumption)                |
| Xác thực địa chỉ email                                    | Working (assumption)                |
| Quên mật khẩu (email hoặc sđt)                            | Working                             |
Còn thêm?

**Note**: Firebase không có tạo tài khoản được bằng số điện thoại nên số điện thoại chỉ được dùng làm đăng nhập hoặc quên mật khẩu

#### Màn hình chính
| Task                                                       | Trạng thái       |
|------------------------------------------------------------|------------------|
| Layout homepage                                            | Complete         |
| Liệt kê danh sách loại sản phẩm?                           | Working, limited |
| Liệt kê danh sách sản phẩm nổi bật                         | Working, limited |
| Khung tìm kiếm sản phẩm chuyển sang danh sách sản phẩm     | Working          |
| Nhấn vào sản phẩm chuyển sang giao diện thông tin sản phẩm | Working          |
Còn thêm?

#### Màn hình tìm kiếm sản phẩm

| Task                                                       | Trạng thái       |
|------------------------------------------------------------|------------------|
| Layout tìm kiếm                                            | Complete         |
| Layout ưa thích                                            | Complete         |
| Liệt kê danh sách các sản phẩm                             | Working          |
| Khung tìm kiếm theo tên / loại sản phẩm                    | Working, limited |
| Nhấn vào sản phẩm chuyển sang giao diện thông tin sản phẩm | Working          |
Còn thêm?

#### Màn hình thông tin sản phẩm
| Task                           | Trạng thái |
|--------------------------------|------------|
| Layout thông tin sản phẩm      | Complete   |
| Layout thông tin loại sản phẩm | Complete   |
| Hiển thị thông tin sản phẩm    | Working    |
| Nút chọn thêm vào mục ưa thích | Working    |
| Nút đặt mua sản phẩm           | Working    |
Còn thêm?

#### Màn hình giỏ hàng

| Task                                               | Trạng thái |
|----------------------------------------------------|------------|
| Layout giỏ hàng                                    | Complete   |
| Layout cổng thanh toán                             | Skipped    |
| Layout xác nhận thanh toán                         | Complete   |
| Liệt kê danh sách sản phẩm trong giỏ hàng hiện tại | Working    |
| Đặt mua và thanh toán (giả)                        | Working    |
Còn thêm?

#### Màn hình quản lý tài khoản
| Task                                                                    | Trạng thái           |
|-------------------------------------------------------------------------|----------------------|
| Layout thông tin người dùng                                             | Complete             |
| Layout chỉnh sửa thông tin                                              | Complete             |
| Layout quên mật khẩu / OTP                                              | Complete             |
| Liệt kê danh sách các sản phẩm ưa thích trong 1 tab khác                | Working              |
| Xem và thay đổi thông tin cá nhân                                       | Working              |
| Thay đổi ảnh đại diện (vì bth ko app nào đặt ảnh đại diện ngay lúc reg) | Working              |
| Xem những hoá đơn đã hoàn thành trước đó                                | Không có layout      |
| Liên kết tài khoản Google / Facebook (nếu không có)                     | Working (assumption) |
| Đăng xuất                                                               | Working              |

Còn thêm?

#### Màn hình chat người dùng

| Task                          | Trạng thái           |
|-------------------------------|----------------------|
| Layout lịch sử chat           | Complete             |
| Layout chat với người dùng    | Complete             |
| Chức năng chat với người dùng | Working (assumption) |

Còn thêm?

#### Màn hình thông tin người dùng

| Task                                | Trạng thái |
|-------------------------------------|------------|
| Layout thông tin người dùng         | Complete   |
| Nút chuyển sang chat với người dùng | Working    |

Còn thêm?

#### Cần thêm

- ... không kịp

### Các chức năng khác sẽ có hoặc không

- Giao diện người bán
- Giao diện người giao hàng
- Live chat với người bán / người giao (phải có app người bán / người giao)
  Nếu không thì nhét chung cả chức năng bán vào người dùng thông thường -> người dùng có thể bán và
  mua cùng lúc

## Chú ý
- Sử dụng Tiếng Anh (sớm sau gì tất cả code đều làm bằng tiếng Anh cả thôi)
- Khi đặt tên View trong layout thì theo format ``<activity>_<loại view>_<tên view>``. Ví dụ như ``login_btn_submit``. Đừng có đặt tùm lum như textView2 textView3
