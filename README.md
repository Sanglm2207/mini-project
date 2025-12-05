# Hệ Thống Thông Báo Thời Gian Thực

Dự án này là một bài kiểm tra kỹ thuật được thiết kế để đánh giá khả năng của ứng viên trong việc xây dựng một hệ thống backend hiện đại. Ứng viên sẽ làm việc với một dự án Spring Boot đã được khởi tạo sẵn và hoàn thiện các tính năng được yêu cầu, tập trung vào giao tiếp thời gian thực, xử lý sự kiện bất đồng bộ và tương tác với các hệ cơ sở dữ liệu khác nhau.

## I. Công nghệ sử dụng (Technology Stack)
- **Ngôn ngữ:** Java 21
- **Framework:** Spring Boot 3.3.0
- **Database:** MongoDB
- **Message Broker:** Apache Kafka
- **In-memory Store / Cache:** Redis
- **Real-time Communication:** WebSocket (thuần, không STOMP)
- **Containerization:** Docker & Docker Compose
- **Build Tool:** Maven

## II. Chuẩn bị (Prerequisites)
Trước khi bắt đầu, hãy đảm bảo bạn đã cài đặt các công cụ sau trên máy của mình:
- **Git:** Để clone dự án.
- **JDK 21:** Dự án được viết bằng Java 21.
- **Apache Maven:** Để quản lý và xây dựng dự án.
- **Docker & Docker Compose:** Để chạy các dịch vụ nền tảng (database, message broker...).
- **IDE:** IntelliJ IDEA (khuyến nghị) hoặc một IDE khác hỗ trợ Java và Spring Boot.
- **Postman:** (Hoặc một công cụ tương tự) để kiểm tra API và kết nối WebSocket.

## III. Hướng dẫn cài đặt và chạy (Setup and Run)

Thực hiện theo các bước sau để khởi chạy dự án trên môi trường local.

#### 1. Clone dự án
Mở terminal và clone repository về máy của bạn:
```bash
git clone <URL_CUA_DU_AN>
cd mini-project
```

####  2. Khởi động các dịch vụ nền tảng (Docker)
```bash
   docker-compose up -d
  ```
####  3. Mở dự án và cài đặt Dependencies
   Mở dự án bằng IDE của bạn. Maven sẽ tự động tải về tất cả các dependencies đã được định nghĩa trong file pom.xml.
####  4. Cấu hình IDE
   Hãy chắc chắn rằng IDE của bạn được cấu hình để sử dụng JDK 21 cho dự án này.
####  5. Chạy ứng dụng
   Tìm và chạy file Application.java. Server Spring Boot sẽ khởi động và chạy trên cổng 8080.
####  6. Kiểm tra
   Nếu mọi thứ thành công, ứng dụng sẽ chạy tại địa chỉ http://localhost:2025. Bạn có thể sử dụng Postman để kiểm tra các API ban đầu (đăng ký, đăng nhập) và xác nhận môi trường đã sẵn sàng.

## IV. Task Requirements
Dự án đã được cấu hình sẵn hệ thống xác thực (JWT) và kết nối WebSocket cơ bản.

**Lưu ý quan trọng:** Tất cả các tương tác chính của người dùng (sau khi kết nối) sẽ được thực hiện thông-qua việc gửi các tin nhắn JSON có cấu trúc qua kết nối WebSocket, thay vì gọi các REST API.

#### Định dạng Tin nhắn (Message Format)
Mọi tin nhắn từ client đến server phải tuân theo cấu trúc sau:
```json
{
  "type": "ACTION_TYPE",
  "payload": {
    "key1": "value1",
    "key2": "value2"
  }
}
```

### ```Req-1: Gửi và Lưu trữ Tin nhắn Thông báo```
*   **Mô tả:**  Người dùng gửi một tin nhắn cho người dùng khác. Server sẽ lưu tin nhắn và chuẩn bị để chuyển tiếp nó.
  *   **Client Action:** Gửi một message với type: "SEND_NOTIFICATION".
  ```json
      {
        "type": "SEND_NOTIFICATION",
        "payload": {
             "recipientId": "some-user-id",
             "content": "Chào bạn, bạn khỏe không?"
        }
     }
   ```
```yaml
    - Logic:
        1. Trong handleTextMessage, nhận và parse message.
        2. Lấy senderId từ session.getAttributes().
        3. Tạo một đối tượng Notification với các trường: senderId, recipientId, content, timestamp, và status: UNREAD.
        4. Lưu đối tượng Notification vào collection notifications trong MongoDB.
        5. Logic chuyển tiếp sẽ được xử lý ở Req-2.
````

### ```Req-2: Tích hợp Kafka để Xử lý Thông báo Bất đồng bộ```
*   **Mô tả:** Tách rời logic gửi thông báo WebSocket ra khỏi luồng xử lý của API bằng cách sử dụng Kafka.
*   **Chi tiết:**
```yaml
- Trigger: Sau khi lưu `Notification` thành công ở Req-1.
- Logic:
    1. Sau khi lưu Notification thành công ở Yêu cầu 1, publish một message (dạng JSON của đối tượng Notification) vào Kafka topic có tên là notification_events.
    2. Tạo một KafkaConsumerService với @KafkaListener để lắng nghe topic notification_events.
    3. Khi consumer nhận được message, nó sẽ:
      3.1. Deserialize message trở lại thành đối tượng Notification.
      3.2. Kiểm tra xem recipientId có đang online không (thông qua WebSocketSessionManager).
      3.3. Nếu có, gửi một message WebSocket mới đến client của người nhận với type: "RECEIVE_NOTIFICATION" và payload là toàn bộ đối tượng Notification.
```

### ```Req-3: Quản lý Trạng thái Online của Người dùng bằng Redis```
*   **Mô tả:** Người dùng có thể hỏi trạng thái online của một người dùng khác.
*   **Chi tiết:** Gửi một message với type: "GET_USER_STATUS".
```json
      {
          "type": "GET_USER_STATUS",
          "payload": {
            "userId": "some-user-id-to-check"
          }
      }
```
```yaml
- Cập nhật CustomWebSocketHandler (Logic nền): Sau khi lưu `Notification` thành công ở Req-1.
    - Trong afterConnectionEstablished: Lưu trạng thái online của user vào Redis. Ví dụ: SET user:status:<userId> online.
    -  Trong afterConnectionClosed: Xóa trạng thái của user khỏi Redis.
- Xử lý Message:
    1. Khi nhận được message GET_USER_STATUS trong handleTextMessage:
    2. Đọc trạng thái của userId trong payload từ Redis.
    3. Gửi một message WebSocket phản hồi lại cho chính client đã gửi yêu cầu với type: "USER_STATUS_RESPONSE" và payload là {"userId": "...", "status": "online | offline"}.
```

### ```Req-4: Đánh dấu "Đã xem" & Thông báo lại cho Người gửi (Sử dụng MongoDB Change Streams)```
*   **Mô tả:** Người dùng đánh dấu một tin nhắn là đã đọc. Server cập nhật DB và thông báo ngược lại cho người gửi gốc bằng MongoDB Change Streams.
*   **Chi tiết:** Gửi một message với type: "MARK_NOTIFICATION_READ".
```json
      {
          "type": "MARK_NOTIFICATION_READ",
          "payload": {
            "notificationId": "some-notification-id"
          }
      }
```
```yaml
   -  Xử lý Message:
      - Khi nhận message MARK_NOTIFICATION_READ, gọi service để cập nhật trường status của Notification trong MongoDB thành READ.
    - Service Nền (MongoDB Change Streams):
      - Tạo một service chạy nền để lắng nghe sự kiện update trên collection notifications.
      -  Khi bắt được sự kiện một Notification có status đổi thành READ, service này sẽ:
      -  Lấy ra senderId của Notification đó.
      - Nếu senderId đang online, gửi một message WebSocket đến client của họ với type: "NOTIFICATION_READ_UPDATE" và payload là {"notificationId": "..."}.
```


### ```Req-5: Kênh Thông báo Chung (Sử dụng Redis Pub/Sub)```
*   **Mô tả:** Xây dựng một tính năng cho phép admin gửi thông báo đến **tất cả** người dùng đang online. Tính năng này phải có khả năng mở rộng trên nhiều instance, do đó cần sử dụng Redis Pub/Sub.
*   **Chi tiết:** Gửi một message với type: "BROADCAST_MESSAGE". (chỉ ADMIN)
```json
      {
          "type": "BROADCAST_MESSAGE",
          "payload": {
            "message": "Bảo trì hệ thống lúc 10h tối nay!"
          }
      }
```
```yaml
    - Server Logic:
        - Xử lý Message:
              - Khi nhận message BROADCAST_MESSAGE, lấy userRole từ session.getAttributes().
              - Kiểm tra quyền: Nếu role không phải là ADMIN, gửi lại message lỗi và dừng lại.
              - Nếu là ADMIN, publish message trong payload vào một channel của Redis có tên là public_notifications.
        - Service Nền (Redis Subscriber):
            - Tạo một RedisSubscriber để lắng nghe channel public_notifications.
            - Khi nhận được message từ channel, nó sẽ lặp qua tất cả các session trong WebSocketSessionManager.
            - Gửi message đó đến tất cả các client đang kết nối với type: "GLOBAL_ANNOUNCEMENT" và payload là {"message": "..."}.
```
