# Firebase Authentication App (Android)
## Preview App

<p align="center">
  <img src="https://i.imgur.com/WVtDAjO.png" alt="Sign In Screen"/>
  <br><em>*Màn hình Đăng Nhập (Sign In Screen)*</em>
</p>

<p align="center">
  <img src="https://i.imgur.com/y8Rbq7d.png" alt="Sign Up Screen"/>
  <br><em>*Màn hình Đăng Ký (Sign Up Screen)*</em>
</p>

<p align="center">
  <img src="https://i.imgur.com/GenXqmN.png" alt="Sign Up Screen OTP Send"/>
  <br><em>*Màn hình Chính Send OTP (Home Screen Seen OTP)*</em>
</p>

<p align="center">
  <img src="https://i.imgur.com/ucXO72U.png" alt="Home Screen"/>
  <br><em>*Màn hình Chính (Home Screen)*</em>
</p>


Ứng dụng Android sử dụng Firebase Authentication để cung cấp các tính năng đăng nhập, đăng ký và xác thực người dùng qua email/mật khẩu, số điện thoại và Google. Ứng dụng hỗ trợ:

- Đăng nhập và đăng ký bằng Google.
- Đăng nhập và đăng ký bằng email và mật khẩu với các ràng buộc dữ liệu.
- Xác thực số điện thoại qua OTP.
- Xác nhận email người dùng.

## Tính Năng

1. **Đăng nhập bằng Google:**
   - Nếu người dùng chưa có tài khoản Firebase, hệ thống sẽ tự động tạo tài khoản mới và lưu vào Firebase.

2. **Đăng nhập bằng Email và Mật khẩu:**
   - Người dùng có thể đăng nhập bằng email và mật khẩu đã đăng ký.

3. **Đăng ký tài khoản bằng Email và Mật khẩu:**
   - Ràng buộc dữ liệu khi đăng ký:
     - Email không được bỏ trống và phải đúng định dạng.
     - Mật khẩu phải dài hơn 6 ký tự.
     - Mật khẩu nhập lại phải khớp với mật khẩu đã nhập.

4. **Xác thực qua số điện thoại (OTP):**
   - Người dùng sẽ nhận OTP qua số điện thoại và phải xác thực trong khoảng thời gian nhất định.
   - Mỗi OTP chỉ có thể gửi mỗi phút và chỉ được yêu cầu lại sau ít nhất 1 phút.

5. **Xác nhận Email:**
   - Sau khi đăng nhập thành công, nếu email chưa được xác nhận, hệ thống sẽ hiển thị trạng thái "Email chưa được xác nhận".
   - Người dùng có thể yêu cầu gửi lại liên kết xác nhận qua email, khi nhấn vào liên kết, trạng thái email sẽ chuyển thành "Đã xác nhận".

6. **Màn hình chính (Home Screen):**
   - Hiển thị video ngẫu nhiên mà bạn đã chèn.
   - Thông báo trạng thái xác nhận email và email người dùng.

## Cấu Trúc Dự Án

- **SignInActivity (Màn hình Đăng Nhập)**: Màn hình đăng nhập người dùng.
- **SignUpActivity (Màn hình Đăng Ký)**: Màn hình đăng ký người dùng mới.
- **HomeActivity (Màn hình Chính)**: Màn hình chính hiển thị sau khi người dùng đăng nhập thành công.

## Cài Đặt và Hướng Dẫn

### Bước 1: Cài Đặt Firebase

1. Truy cập [Firebase Console](https://console.firebase.google.com/).
2. Tạo một project mới và kết nối với ứng dụng Android của bạn.
3. Cấu hình Firebase Authentication:
   - Kích hoạt đăng nhập Google.
   - Kích hoạt đăng nhập bằng email và mật khẩu.
   - Kích hoạt xác thực qua số điện thoại.

### Bước 2: Cấu Hình Ứng Dụng Android

1. Thêm các dependencies Firebase trong file `build.gradle`:
