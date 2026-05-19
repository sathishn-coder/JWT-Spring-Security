# JWT-Spring-Security

# 📦 E-Commerce Product API — Spring Boot + JWT Security

A secure REST API built using Spring Boot, Spring Security, JWT Authentication, and MySQL for user management and product management with role-based access control.

---

# 🚀 Features

- User Registration & Login
- JWT Token Authentication
- Role-Based Authorization (`ROLE_USER`, `ROLE_ADMIN`)
- Secure REST APIs
- Product CRUD Operations
- Admin Panel APIs
- Password Encryption using BCrypt
- Global Exception Handling
- Validation Support

---

# 🛠️ Tech Stack

| Technology | Usage |
|---|---|
| Java 17 | Backend Language |
| Spring Boot | Application Framework |
| Spring Security | Authentication & Authorization |
| JWT | Token-based Security |
| MySQL | Database |
| Maven | Dependency Management |
| Hibernate / JPA | ORM |

---

# 🔐 Authentication Flow

1. Register a new account
2. Login using username & password
3. Copy JWT token from login response
4. Add token in request header:

```http
Authorization: Bearer your_jwt_token
```

---

# 📌 API Endpoints

---

# 🌐 Public APIs

These APIs do not require authentication.

---

## 1️⃣ Register User

### Endpoint

```http
POST /api/auth/register
```

### Request Body

```json
{
  "firstName": "John",
  "lastName": "Doe",
  "username": "johndoe",
  "email": "john@example.com",
  "password": "SecurePass@1"
}
```

### Success Response

```json
{
  "success": true,
  "message": "User registered successfully"
}
```

---

## 2️⃣ Login User

### Endpoint

```http
POST /api/auth/login
```

### Request Body

```json
{
  "username": "admin",
  "password": "Admin@1234"
}
```

### Success Response

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

# 🔒 Protected APIs

These APIs require JWT Token in the Authorization Header.

```http
Authorization: Bearer <your_token>
```

---

# 👤 User APIs

---

## 3️⃣ Get Logged-in User Profile

### Endpoint

```http
GET /api/user/profile
```

### Access

- USER
- ADMIN

### Headers

```http
Authorization: Bearer <token>
```

### Success Response

```json
{
  "success": true,
  "data": {
    "username": "johndoe",
    "email": "john@example.com",
    "role": "ROLE_USER"
  }
}
```

---

# 🛒 Product APIs

---

## 4️⃣ Create Product

### Endpoint

```http
POST /api/products
```

### Access

- ADMIN only

### Headers

```http
Authorization: Bearer <admin_token>
Content-Type: application/json
```

### Request Body

```json
{
  "name": "Apple MacBook Pro",
  "description": "M3 chip, 16GB RAM, 512GB SSD",
  "price": 1999.99,
  "stockQuantity": 50,
  "category": "Laptops",
  "isAvailable": true
}
```

### Success Response

```json
{
  "success": true,
  "message": "Product created successfully"
}
```

---

## 5️⃣ Get All Products

### Endpoint

```http
GET /api/products
```

### Access

- USER
- ADMIN

### Headers

```http
Authorization: Bearer <token>
```

### Success Response

```json
{
  "success": true,
  "message": "Fetched 2 products",
  "data": [
    {
      "id": 1,
      "name": "Apple MacBook Pro",
      "price": 1999.99,
      "category": "Laptops",
      "isAvailable": true
    },
    {
      "id": 2,
      "name": "Samsung Galaxy S24",
      "price": 899.99,
      "category": "Phones",
      "isAvailable": true
    }
  ]
}
```

---

## 6️⃣ Update Product

### Endpoint

```http
PUT /api/products/{id}
```

### Example

```http
PUT /api/products/1
```

### Access

- ADMIN only

### Request Body

```json
{
  "name": "Apple MacBook Pro M3",
  "description": "Updated: M3 Pro chip, 18GB RAM",
  "price": 2199.99,
  "stockQuantity": 40,
  "category": "Laptops",
  "isAvailable": true
}
```

### Success Response

```json
{
  "success": true,
  "message": "Product updated successfully"
}
```

---

## 7️⃣ Delete Product

### Endpoint

```http
DELETE /api/products/{id}
```

### Example

```http
DELETE /api/products/1
```

### Access

- ADMIN only

### Success Response

```json
{
  "success": true,
  "message": "Product deleted successfully"
}
```

---

# 🛡️ Admin APIs

---

## 8️⃣ Get All Users

### Endpoint

```http
GET /api/admin/users
```

### Access

- ADMIN only

### Headers

```http
Authorization: Bearer <admin_token>
```

### Success Response

```json
{
  "success": true,
  "message": "Found 2 users",
  "data": [
    {
      "id": 1,
      "username": "admin",
      "email": "admin@example.com",
      "role": "ROLE_ADMIN",
      "isActive": true
    },
    {
      "id": 2,
      "username": "user",
      "email": "user@example.com",
      "role": "ROLE_USER",
      "isActive": true
    }
  ]
}
```

---

## 9️⃣ Update User Role

### Endpoint

```http
PUT /api/admin/users/{id}
```

### Example

```http
PUT /api/admin/users/2
```

### Access

- ADMIN only

### Request Body

```json
{
  "role": "ROLE_ADMIN",
  "isActive": true
}
```

### Success Response

```json
{
  "success": true,
  "message": "User updated successfully"
}
```

---

# 🗄️ Default Admin Credentials

```json
{
  "username": "admin",
  "password": "Admin@1234"
}
```

---

# ⚙️ Application Setup

## 1️⃣ Clone Repository

```bash
git clone <your-repository-url>
```

---

## 2️⃣ Configure Database

Update `application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db
spring.datasource.username=root
spring.datasource.password=your_password
```

---

## 3️⃣ Run Application

```bash
mvn spring-boot:run
```

---

# 🧪 Testing APIs

You can test APIs using:

- Postman
- Swagger UI
- Insomnia

---

# 📂 Project Structure

```text
src/main/java
│
├── controller
├── service
├── repository
├── entity
├── dto
├── security
├── config
├── exception
└── util
```

---

# ✅ Security Features

- JWT Authentication
- BCrypt Password Encoding
- Role-Based Access Control
- Secure Endpoints
- Stateless Session Management

---

# 👨‍💻 Author

Sathish N

Backend Developer | Java | Spring Boot | MySQL | REST API
