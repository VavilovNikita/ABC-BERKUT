# Telegram Bot API

REST API-сервис для:
- регистрации пользователей
- аутентификации
- отправки и получения сообщений
- генерации Telegram-токена

---

## 🚀 Доступ к API

API уже развернуто и доступно по адресу:  
http://89.111.141.165

---

## 📦 Эндпоинты

🔐 Регистрация  
POST http://89.111.141.165/api/auth/register  
Тело запроса:
{
"username": "testuser",
"password": "123456",
"name": "Никита"
}

🔐 Вход (аутентификация)  
POST http://89.111.141.165/api/auth/login  
Тело запроса:
{
"username": "testuser",
"password": "123456"
}  
Ответ:
Authorization: Bearer <JWT_токен>

📬 Отправка сообщения  
GET http://89.111.141.165/api/messages/send  
Заголовки:  
Authorization: Bearer <JWT_токен>  
Тело запроса:
{
"message": "Привет"
}

📥 Получение сообщений  
GET http://89.111.141.165/api/messages/all  
Заголовки:  
Authorization: Bearer <JWT_токен>  
Ответ: список сообщений текущего пользователя

🤖 Генерация Telegram-токена  
POST http://89.111.141.165/api/telegram/token  
Заголовки:  
Authorization: Bearer <JWT_токен>  
Ответ:  
Telegram-токен в формате UUID

---

## 🐳 Быстрый запуск через Docker

1. Клонируй репозиторий:
   git clone https://github.com/VavilovNikita/ABC-BERKUT.git  
   cd ABC-BERKUT

2. Собери и запусти контейнеры:
   docker-compose up --build -d

3. Убедись, что контейнеры работают:
   docker ps

По умолчанию:
- Backend доступен на http://localhost:8079

---

## 🧪 Примеры curl-запросов

Регистрация:
curl -X POST http://89.111.141.165/api/auth/register \
-H "Content-Type: application/json" \
-d '{"username":"testuser","password":"123456","name":"Никита"}'

Авторизация:
curl -X POST http://89.111.141.165/api/auth/login \
-H "Content-Type: application/json" \
-d '{"username":"testuser","password":"123456"}'

Отправка сообщения:
curl -X GET http://89.111.141.165/api/messages/send \
-H "Content-Type: application/json" \
-H "Authorization: Bearer <ваш_токен>" \
-d '{"message":"Привет"}'

---
