# Описание проектирования программного обеспечения (SDD) SportSpot
**Версия:** 0.3  
**Статус:** Согласовано  
**Система:** Веб-сервис онлайн-бронирования спортивных площадок SportSpot  
**Исполнитель:** П. С. Шмыгин (гр. 450503)  
**Заказчик:** В. П. Бышкало  

---

## 1 Введение
Документ составлен по стандарту IEEE 1016 на основе согласованной спецификации требований SRS v0.2 и запроса на изменение CR-001.

### 1.1 Заинтересованные стороны
- Клиенты-спортсмены (гости и зарегистрированные пользователи);
- Администраторы спортивных комплексов и кортов;
- Системный администратор;
- Заказчик (В. П. Бышкало);
- Ведущий разработчик-архитектор (П. С. Шмыгин);
- Junior-разработчик;
- Оператор развёртывания.

---

## 2 Архитектурные представления (Design Views)

### 2.1 Контекстное представление (Context View)
Система взаимодействует со следующими внешними сущностями:
- Пользователь через Web SPA (браузеры Chrome, Firefox, Safari);
- СУБД PostgreSQL 16 для хранения данных пользователей, кортов, расписаний и бронирований;
- Внешний шлюз интернет-эквайринга (банк) для обработки карточных платежей;
- Внешний SMTP-шлюз для отправки электронных чеков и билетов с QR-кодами.

### 2.2 Композиционное представление (Composition View)
Архитектурный стиль – модульный монолит (Modular Monolith) на Java 21 / Spring Boot 3:
1. web-client – SPA на React / TypeScript (каталог, сетка расписания, чекаут, сплит-оплата);
2. uth-security – регистрация, вход, JWT, RBAC, аудит входа;
3. catalog-venue – спорткомплексы, типы покрытий, корты, фотогалерея, правила;
4. schedule-slots – сетка 60-мин слотов с 07:00 до 23:00, динамические тарифы, блокировка на ТО;
5. ooking-core – пессимистическая блокировка слотов (SELECT ... FOR UPDATE), таймаут 10 мин, отмена;
6. illing-subscriptions – абонементы (10/20 ч), сплит-оплата (CR-001), вебхуки банка;
7. 
eviews-rating – верифицированные отзывы, модерация, композитный рейтинг;
8. 
otifications – асинхронная генерация и отправка email-билетов;
9. infrastructure – PostgreSQL, Flyway, Docker Compose, метрики.

### 2.3 Логическое представление (Logical View)
Связи направлены сверху вниз: UI -> Controllers -> Application Services -> Domain Services -> Repositories -> PostgreSQL. Модули доменной логики изолированы от СУБД и веб-интерфейса.

### 2.4 Информационное представление (Information View)
Основные сущности:
- User (id, email, password_hash, role, full_name, phone)
- Venue (id, name, address, description, opening_hours)
- Court (id, venue_id, name, surface_type, indoor, base_hourly_rate)
- TimeSlot (id, court_id, date, start_time, end_time, status, dynamic_rate)
- Booking (id, user_id, court_id, timeslot_id, status, total_price, created_at)
- Subscription (id, user_id, hours_total, hours_remaining, expires_at)
- Equipment (id, venue_id, item_type, available_quantity, hourly_price)
- Review (id, user_id, court_id, rating, comment, is_verified, created_at)
- SplitPaymentGroup (id, booking_id, total_amount, timeout_at, status)
- PaymentShare (id, group_id, user_email, amount, status, payment_id)

### 2.5 Представление интерфейсов (Interface View)
- REST API для каталога, бронирования, аутентификации и чекаута;
- Сценарий сплит-оплаты: капитан выбирает долевую оплату, генерируется ссылка/QR, участники оплачивают свои доли, статус обновляется по вебхукам банка.

### 2.6 Представление развёртывания (Deployment View)
Развёртывание осуществляется на виртуальном сервере через Docker Compose (контейнеры приложения и PostgreSQL 16).
