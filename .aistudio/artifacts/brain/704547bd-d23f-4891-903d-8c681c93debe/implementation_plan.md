# Kế Hoạch: Tùy Chọn Ứng Dụng Chia Đôi Màn Hình (Split Screen App Picker) & MIUI 14 Floating Window

Cung cấp tính năng quét toàn bộ ứng dụng đã cài đặt trên điện thoại Xiaomi POCO, cho phép tài xế tự do chọn 2 ứng dụng bất kỳ để hiển thị chia đôi trên giao diện lái xe xe hơi, hỗ trợ mở dạng Cửa sổ nổi (Floating Window MIUI 14) song song với trình phát media / bản đồ.

## User Review & Critical Decisions

> [!IMPORTANT]
> Dựa trên câu trả lời phỏng vấn của bạn:
> - **Cơ chế chọn app**: Ứng dụng sẽ quét toàn bộ danh sách ứng dụng người dùng đã cài trên điện thoại (`PackageManager`) kèm biểu tượng và tên ứng dụng để người dùng chủ động chỉ định cho Nửa trên (Khung 1) và Nửa dưới (Khung 2).
> - **Cơ chế vận hành trên MIUI 14**: Kết hợp cả hiển thị trực tiếp trong AutoDrive (đối với Bản đồ, YouTube, Web Player) và cơ chế kích hoạt **Cửa sổ nổi (MIUI 14 Floating Window)** cho các ứng dụng native cài trên máy để chạy song song mượt mà trên màn hình ô tô.

- **Quyết định đã xác nhận 1**: Cho phép quét toàn bộ ứng dụng cài trên máy và lưu cấu hình 2 ứng dụng chia đôi mặc định.
- **Quyết định đã xác nhận 2**: Hỗ trợ mở Cửa sổ nổi MIUI 14 (Floating Window) kết hợp trình phát web/media chuyên dụng khi lái xe.
- **Tính năng bổ sung**: Tích hợp nút đổi nhanh 1-chạm (Quick Swap & Picker Modal) ngay trên thanh chia đôi màn hình để không cần vào cài đặt phức tạp khi đang ngồi trong xe.

---

## 1. Tổng Quan & Trải Nghiệm Lái Xe (Overview & Core Concept)

- **Mục tiêu**: Người dùng không bị bó buộc vào bản đồ hay YouTube mặc định; có thể tùy chọn bất kỳ ứng dụng nào mình yêu thích (ví dụ: Google Maps + Zing MP3, Waze + YouTube, Vietmap + Spotify, hoặc Zalo + Google Maps).
- **Đối tượng**: Tài xế xe ô tô sử dụng điện thoại Xiaomi / POCO MIUI 14 kết nối qua Android Auto, giá đỡ taplo hoặc màn hình Android ô tô (qua HDMI/Cast).
- **Giá trị cốt lõi**:
  - Cá nhân hóa 100% 2 nửa màn hình lái xe.
  - Thao tác chọn ứng dụng trực quan với danh sách có tìm kiếm, icon sắc nét và phân loại rõ ràng.
  - Khắc phục triệt để hạn chế của Android Auto nguyên bản bằng cách tận dụng tính năng Cửa sổ nổi của MIUI 14.

---

## 2. Trải Nghiệm Người Dùng & Giao Diện (UX & Visual Design)

### Luồng tương tác chính:
1. **Thanh chọn trên giao diện Split Screen**:
   - Ở mỗi nửa màn hình (Pane 1 & Pane 2), hiển thị thanh tiêu đề kèm nút:
     - Tên ứng dụng hiện tại + icon.
     - Nút **"Đổi Ứng Dụng"** (kèm icon chuyển đổi).
     - Nút **"Mở Cửa Sổ Nổi MIUI"** (phím tắt mở app đè lên màn hình taplo).
     - Nút **"Hoán Đổi 2 Bên"** (đảo vị trí trên/dưới hoặc trái/phải).
2. **Modal Chọn Ứng Dụng (App Selector Sheet / Dialog)**:
   - Mở giao diện BottomSheet / Dialog tối giản, chuẩn phong cách ô tô (chữ to, nền tối chống chói).
   - Thanh tìm kiếm ứng dụng nhanh (Search by app name).
   - Tab phân loại: **Tất Cả Ứng Dụng Đã Cài**, **Ứng Dụng Xe Hơi Đề Xuất** (Maps, Waze, YouTube, Spotify, Zing MP3, VOV), **Ứng Dụng Tích Hợp Sẵn**.
   - Chạm vào ứng dụng để gán ngay lập tức vào khung đã chọn.
3. **Cơ chế hiển thị thông minh**:
   - Nếu là ứng dụng web/video/maps (YouTube, Google Maps, OpenStreetMap, v.v.): Hiển thị nhúng trực tiếp trong khung chia.
   - Nếu là ứng dụng native hệ thống: Hiển thị giao diện điều khiển nhanh, kèm nút khởi chạy Cửa sổ nổi (Floating Window) hoặc Split-Screen của MIUI 14 chỉ với 1 chạm.

---

## 3. Kiến Trúc Kỹ Thuật (Technical Architecture)

```
┌─────────────────────────────────────────────────────────────┐
│                    DashboardScreen                          │
│                                                             │
│  ┌───────────────────────┐       ┌───────────────────────┐  │
│  │     PANE 1 (Top/Left) │       │   PANE 2 (Bottom/Right│  │
│  │   [App 1 Header &     │       │   [App 2 Header &     │  │
│  │    Quick Swap Action] │       │    Quick Swap Action] │  │
│  │  - Web Content HOẶC   │       │  - YouTube HOẶC       │  │
│  │  - Native Quick Launch│       │  - Native Quick Launch│  │
│  │    MIUI Floating Win  │       │    MIUI Floating Win  │  │
│  └───────────────────────┘       └───────────────────────┘  │
│               ▲                             ▲               │
└───────────────┼─────────────────────────────┼───────────────┘
                │                             │
    ┌───────────┴─────────────────────────────┴───────────┐
    │              InstalledAppsRepository                │
    │  - PackageManager: queryIntentActivities            │
    │  - Lấy danh sách app cài trên điện thoại (APK)      │
    │  - Nhớ cấu hình đã chọn (SharedPreferences)         │
    │  - Trình kích hoạt Cửa sổ nổi MIUI 14 Intent       │
    └─────────────────────────────────────────────────────┘
```

### Các thành phần chính:
1. **`InstalledAppInfo` Data Model**:
   - `packageName`: Tên gói ứng dụng (ví dụ: `com.google.android.apps.maps`)
   - `appName`: Tên hiển thị (ví dụ: Google Maps, Spotify)
   - `icon`: Drawable icon hoặc icon hệ thống
   - `isLaunchable`: Khả năng mở trực tiếp
   - `isBuiltInWeb`: Cờ đánh dấu app có thể hiển thị trực tiếp trên web view ô tô
2. **`InstalledAppsManager`**:
   - Quét danh sách ứng dụng đã cài bằng `packageManager.queryIntentActivities(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER), 0)`.
   - Lưu trữ 2 app được chọn vào SharedPreferences / StateFlow.
   - Hàm `launchAsFloatingWindow(context, packageName)`: Khởi chạy ứng dụng với cấu hình cờ MIUI Freeform / Floating Window.
3. **`AppPickerBottomSheet`**:
   - Giao diện chọn ứng dụng với thanh tìm kiếm thời gian thực.
   - Lưới hiển thị icon lớn tối thiểu 56dp đảm bảo bấm chính xác khi trên xe.
4. **Cập nhật `DashboardScreen`**:
   - Thay thế liên kết cứng bản đồ/YouTube bằng 2 Container động (`DynamicPaneContainer`), phản hồi theo ứng dụng người dùng đã chọn.

---

## 4. Kế Hoạch Triển Khai (Implementation Steps)

1. **Model & Data Layer**:
   - Tạo `InstalledAppInfo.kt` lưu thông tin ứng dụng được quét từ máy.
   - Tạo `InstalledAppsManager.kt` xử lý PackageManager và persistence lưu trữ lựa chọn của người dùng.
2. **Giao Diện Chọn Ứng Dụng (App Picker UI)**:
   - Xây dựng `AppPickerBottomSheet.kt` với thanh tìm kiếm và danh sách ứng dụng mượt mà.
3. **Tích Hợp Vào Màn Hình Chia Đôi (Dashboard Split Integration)**:
   - Cập nhật `CarDriveViewModel.kt` để quản lý `selectedPane1App` và `selectedPane2App`.
   - Nâng cấp `DashboardScreen.kt` bổ sung thanh điều khiển hoán đổi, chọn app và kích hoạt Cửa sổ nổi MIUI 14.
4. **Kiểm Thử & Hoàn Thiện**:
   - Chạy `gradle :app:testDebugUnitTest` và `compile_applet` để đảm bảo không có lỗi biên dịch.
