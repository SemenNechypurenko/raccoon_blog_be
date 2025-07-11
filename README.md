# Raccoon Blog — Backend

This is the backend of **Raccoon Blog** — a collaborative blogging platform that allows users to create posts, upload media, register, and exchange messages. Built with Java Spring Boot and MongoDB, the backend provides a full-featured REST API for the frontend.

## ⚙️ Technologies Used

- Java 17+
- Spring Boot
- MongoDB
- REST API
- Docker & Docker Compose
- JWT authentication (if enabled)
- GitHub Actions CI  
  [CI workflow file](https://github.com/SemenNechypurenko/raccoon_blog_be/blob/master/.github/workflows/ci.yml)

## 🚀 Features

- User registration and authentication
- Creating, editing, and deleting blog posts
- File and media uploads
- Messaging between users
- Fully accessible API for the frontend

## 🐳 Local Setup with Docker

1. Make sure you have **Docker** and **Docker Compose** installed.
2. Clone the repository:

```bash
git clone https://github.com/SemenNechypurenko/raccoon_blog_be.git
cd raccoon_blog_be
Start the application:
bash: docker-compose up --build
The backend will be available at: http://localhost:8080
