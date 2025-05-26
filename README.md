
# Telegram WebApp Authentication — Test Task

## Описание проекта

Это Spring Boot приложение реализует базовую аутентификацию пользователей через Telegram WebApp.  
После успешной валидации данных, полученных от Telegram (`initData`), пользователь аутентифицируется, а его персональные данные сохраняются или обновляются в базе данных.  
На главной странице отображаются основные данные пользователя: `id`, `first_name`, `last_name`, `username`.

---

## Функциональность

- Валидация и проверка подлинности `initData` от Telegram согласно [официальной документации](https://core.telegram.org/bots/webapps#validating-data-received-via-the-mini-app)
- Проверка срока действия данных (обеспечение безопасности)
- Сохранение и обновление информации о пользователе в базе данных (Spring Data JPA)
- Отображение персональных данных пользователя на главной странице (`/`)

## Установка и запуск

1. Клонировать репозиторий:

   ```bash
   git clone https://github.com/ialakey/telegram-auth-test-task
   cd telegram-webapp-auth
   ```

2. Настроить файл `application.properties`:

   ```properties
   telegram.bot-token=YOUR_TELEGRAM_BOT_TOKEN
   ```

3. Собрать проект с помощью Maven:

   ```bash
   mvn clean package
   ```

4. Запустить приложение:

   ```bash
   mvn spring-boot:run
   ```
