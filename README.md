# Cheetah (not fast) Food Delivery
Vì thiếu nhân lực, thời gian và quản lý đội ngũ thì app bị giới hạn rất nhiều so với dự tính ban đầu

### Note:
- Google doesn't like hanging UI thread (main thread) so everything has to be asynchronous
- Need to add check for account details in ``AuthLogin`` since users can skip fill bio 
activity in ``AuthRegister``

*Ưu tiên:
- Đăng nhập được
- Hiển thị sản phẩm
- Tìm kiếm sản phẩm
- Thêm vào giỏ hàng
- Thanh toán
- Người bán xác nhận trạng thái đơn hàng

#### Nhóm 6 (lần 2) - Tổng nghỉ: 4

**Layout**: Chí Cao

**Program**: acay

**Presentation**: Hưng, Phi

~~Rút: Hùng, Hiệp, 2 thằng nữa không biết~~I

## TODO List
### Mục khách hàng
#### Auth Activity - Đăng nhập / Đăng ký
| Task                                                      | Trạng thái          |
|-----------------------------------------------------------|---------------------|
| Layout đăng nhập / đăng ký                                | Hoàn thành          |
| Layout điền thông tin chi tiết sau đăng ký                | Hoàn thành          |
| Layout splash loading screen                              | Hoàn thành          |
| Layout chạy app lần đầu                                   | Sửa lại             |
| Màn hình khởi chạy lần đầu                                | Working             |
| Màn hình yêu cầu thông tin người dùng khi đăng ký lần đầu | Working (in theory) |
| Đăng nhập bằng Google                                     | Working             |
| Đăng nhập bằng Facebook                                   | Not working         |
| Đăng nhập bằng tài khoản thường                           | Chưa test           |
| Đăng ký bằng tài khoản thường                             | Chưa test           |
| Sử dụng tài khoản nền tảng để đăng nhập tài khoản sẵn có  | Working (in theory) |
| Xác thực địa chỉ email                                    | Working (in theory) |
| Quên mật khẩu (email hoặc sđt)                            | Working             |
Còn thêm?

**Note**: Firebase không có tạo tài khoản được bằng số điện thoại nên số điện thoại chỉ được dùng làm đăng nhập hoặc quên mật khẩu

#### Màn hình chính
| Task                                                       | Trạng thái       |
|------------------------------------------------------------|------------------|
| Layout homepage                                            | Hoàn thành       |
| Liệt kê danh sách loại sản phẩm?                           | Working, limited |
| Liệt kê danh sách sản phẩm nổi bật                         | Working, limited |
| Khung tìm kiếm sản phẩm chuyển sang danh sách sản phẩm     | Working          |
| Nhấn vào sản phẩm chuyển sang giao diện thông tin sản phẩm | Not working      |
Còn thêm?

#### Màn hình tìm kiếm sản phẩm

| Task                                                       | Trạng thái       |
|------------------------------------------------------------|------------------|
| Layout tìm kiếm                                            | Hoàn thành       |
| Layout ưa thích                                            | Hoàn thành       |
| Liệt kê danh sách các sản phẩm                             | Working          |
| Khung tìm kiếm theo tên / loại sản phẩm                    | Working, limited |
| Nhấn vào sản phẩm chuyển sang giao diện thông tin sản phẩm | Not working      |
Còn thêm?

#### Màn hình thông tin sản phẩm
| Task                           | Trạng thái |
|--------------------------------|------------|
| Layout thông tin sản phẩm      | Hoàn thành |
| Layout thông tin loại sản phẩm | Hoàn thành |
| Hiển thị thông tin sản phẩm    | Chưa test  |
| Nút chọn thêm vào mục ưa thích | Chưa test  |
| Nút đặt mua sản phẩm           | Chưa test  |
Còn thêm?

#### Màn hình giỏ hàng

| Task                                               | Trạng thái |
|----------------------------------------------------|------------|
| Layout giỏ hàng                                    | Hoàn thành |
| Layout cổng thanh toán                             | Bỏ qua     |
| Layout xác nhận thanh toán                         | Hoàn thành |
| Liệt kê danh sách sản phẩm trong giỏ hàng hiện tại | Chưa test  |
| Đặt mua và thanh toán (giả)                        | Chưa test  |
Còn thêm?

#### Màn hình quản lý tài khoản
| Task                                                                    | Trạng thái          |
|-------------------------------------------------------------------------|---------------------|
| Layout thông tin người dùng                                             | Hoàn thành          |
| Layout chỉnh sửa thông tin                                              | Hoàn thành          |
| Layout quên mật khẩu / OTP                                              | Hoàn thành          |
| Liệt kê danh sách các sản phẩm ưa thích trong 1 tab khác                | Chưa test           |
| Xem và thay đổi thông tin cá nhân                                       | Working (in theory) |
| Thay đổi ảnh đại diện (vì bth ko app nào đặt ảnh đại diện ngay lúc reg) | Working             |
| Xem những hoá đơn đã hoàn thành trước đó                                | Chưa xong           |
| Liên kết tài khoản Google / Facebook (nếu không có)                     | Working (in theory) |
| Đăng xuất                                                               | Working             |

Còn thêm?

#### Màn hình chat người dùng

| Task                          | Trạng thái |
|-------------------------------|------------|
| Layout lịch sử chat           | Hoàn thành |
| Layout chat với người dùng    | Hoàn thành |
| Chức năng chat với người dùng | Chưa test  |

Còn thêm?

#### Màn hình thông tin người dùng

| Task                                | Trạng thái |
|-------------------------------------|------------|
| Layout thông tin người dùng         | Hoàn thành |
| Nút chuyển sang chat với người dùng | Chưa test  |

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
