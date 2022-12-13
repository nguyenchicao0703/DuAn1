# Cheetah (not fast) Food Delivery
Vì thiếu nhân lực, thời gian và quản lý đội ngũ thì app bị giới hạn rất nhiều so với dự tính ban đầu

### Todo before Friday:
- Need to add check for account details in ``AuthLogin`` since users can skip fill bio 
activity in ``AuthRegister``
- ~~**Wrap Glide image loading into a try catch** to prevent crashing from trying to load image 
  without a context when the user quickly dismiss the parent dialog fragment~~
- **Add a total purchase counter to product** upon confirming purchases for use with the 
  Featured menu section in home fragment
- ~~**Move the buttons from recycler views from bottom to top** because having it all the way 
  bottom doesn't make any sense~~
- **More testing**

#### Nhóm 6 (lần 2) - Tổng nghỉ: 5

- **Layout**: Chí Cao
- **Program**: acay
- **Presentation**: Hưng, Cao?
- ~~Rút: Hùng, Hiệp, Phi, 2 thằng nữa không biết~~I

## TODO List
### Mục khách hàng
#### Auth Activity - Đăng nhập / Đăng ký
| Task                                                      | Trạng thái           |
|-----------------------------------------------------------|----------------------|
| Layout đăng nhập / đăng ký                                | Working              |
| Layout điền thông tin chi tiết sau đăng ký                | Working              |
| Layout splash loading screen                              | Working              |
| Layout chạy app lần đầu                                   | Working              |
| Màn hình khởi chạy lần đầu                                | Working              |
| Màn hình yêu cầu thông tin người dùng khi đăng ký lần đầu | Working              |
| Đăng nhập bằng Google                                     | Working              |
| Đăng nhập bằng Facebook                                   | Working              |
| Đăng nhập bằng tài khoản thường                           | Working              |
| Đăng ký bằng tài khoản thường                             | Working              |
| Sử dụng tài khoản nền tảng để đăng nhập tài khoản sẵn có  | Working              |
| Xác thực địa chỉ email                                    | Working (assumption) |
| Quên mật khẩu (email hoặc sđt)                            | Working              |
Còn thêm?

**Note**: Firebase không có tạo tài khoản được bằng số điện thoại nên số điện thoại chỉ được dùng làm đăng nhập hoặc quên mật khẩu

#### Màn hình chính
| Task                                                       | Trạng thái       |
|------------------------------------------------------------|------------------|
| Layout homepage                                            | Working          |
| Liệt kê danh sách sản phẩm mới đăng                        | Working, limited |
| Liệt kê danh sách sản phẩm nổi bật                         | Working, limited |
| Khung tìm kiếm sản phẩm chuyển sang danh sách sản phẩm     | Working          |
| Nhấn vào sản phẩm chuyển sang giao diện thông tin sản phẩm | Working          |
Còn thêm?

#### Màn hình tìm kiếm sản phẩm
| Task                                                            | Trạng thái |
|-----------------------------------------------------------------|------------|
| Layout tìm kiếm                                                 | Working    |
| Layout ưa thích                                                 | Working    |
| Liệt kê danh sách các sản phẩm                                  | Working    |
| Khung tìm kiếm theo tên / loại sản phẩm                         | Working    |
| Thêm mới danh sách khi kéo xuống cuối danh sách khoảng 1.5 giây | Working    |
| Nhấn vào sản phẩm chuyển sang giao diện thông tin sản phẩm      | Working    |
Còn thêm?

#### Màn hình thông tin sản phẩm
| Task                            | Trạng thái |
|---------------------------------|------------|
| Layout thông tin sản phẩm       | Working    |
| Layout thông tin chủ sản phẩm   | Working    |
| Hiển thị thông tin sản phẩm     | Working    |
| Hiển thị thông tin chủ sản phẩm | Working    |
| Nút chọn thêm vào mục ưa thích  | Working    |
| Nút đặt mua sản phẩm            | Working    |
Còn thêm?

#### Màn hình thông tạo bán sản phẩm
| Task                                | Trạng thái      |
|-------------------------------------|-----------------|
| Layout liệt kê các sản phẩm đang có | Working         |
| Layout đăng bán sản phẩm            | Working         |
| Xoá sản phẩm                        | Working         |
| **Thay đổi thông tin sản phẩm**     | Not implemented |

#### Màn hình giỏ hàng
| Task                                               | Trạng thái |
|----------------------------------------------------|------------|
| Layout giỏ hàng                                    | Working    |
| Layout cổng thanh toán                             | Skipped    |
| Layout xác nhận thanh toán                         | Working    |
| Liệt kê danh sách sản phẩm trong giỏ hàng hiện tại | Working    |
| Đặt mua và thanh toán (giả)                        | Working    |
| Xoá giỏ hàng khi thanh toán thành công             | Working    |
Còn thêm?

#### Màn hình quản lý tài khoản
| Task                                                                    | Trạng thái |
|-------------------------------------------------------------------------|------------|
| Layout thông tin người dùng                                             | Working    |
| Layout chỉnh sửa thông tin                                              | Working    |
| Layout quên mật khẩu / OTP                                              | Working    |
| Liệt kê danh sách các sản phẩm ưa thích trong 1 tab khác                | Working    |
| Xem và thay đổi thông tin cá nhân                                       | Working    |
| Thay đổi ảnh đại diện (vì bth ko app nào đặt ảnh đại diện ngay lúc reg) | Working    |
| Xem những hoá đơn đã hoàn thành trước đó                                | Working    |
| Xem thông tin hoá đơn trước đó                                          | Working    |
| Liên kết tài khoản Google / Facebook (nếu không có)                     | Working    |
| Đăng xuất                                                               | Working    |

Còn thêm?

#### Màn hình chat người dùng

| Task                          | Trạng thái |
|-------------------------------|------------|
| Layout lịch sử chat           | Working    |
| Layout chat với người dùng    | Working    |
| Chức năng chat với người dùng | Working    |

Còn thêm?

#### Màn hình thông tin người dùng

| Task                                | Trạng thái |
|-------------------------------------|------------|
| Layout thông tin người dùng         | Working    |
| Nút chuyển sang chat với người dùng | Working    |

Còn thêm?

### Các chức năng khác sẽ có hoặc không

- Giao diện người giao hàng

## Chú ý
- Sử dụng Tiếng Anh (sớm sau gì tất cả code đều làm bằng tiếng Anh cả thôi)
- Khi đặt tên View trong layout thì theo format ``<activity>_<loại view>_<tên view>``. Ví dụ như ``login_btn_submit``. Đừng có đặt tùm lum như textView2 textView3
