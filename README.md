# E-commerce Backend (Spring Boot)
This is the backend service for an e-commerce web application developed using Spring Boot. It provides RESTful APIs to manage authentication, products, shopping cart, orders, users, invoices, and supports payment integration with VNPAY. The project is designed with a clean architecture and includes essential features to run a real-world e-commerce platform.

# Features
Authentication and Authorization
Traditional login using JWT
Google login via OAuth2
Role-based access control (Admin, Staff, User)

# Product Management
CRUD operations for products, categories, and images
Search, filter, and pagination features

# Shopping Cart
Add, remove, update cart items
Automatically create cart per user

# Order Management
Create and manage orders
Update order, payment, and shipping status

# Generate invoices as PDF files

# Payment Integration
Integration with VNPAY (generate payment URL, handle return and IPN notifications)

# Image Storage
Upload and manage images using MinIO (S3-compatible storage)

# Security
JWT-based authentication
Spring Security for role and route protection

# Docker Support
Dockerfile for containerization
Works well with PostgreSQL and MinIO via Docker or external services

# Technologies Used
Java 17
Spring Boot
Spring Security
JWT (JSON Web Token)
Spring Data JPA (Hibernate)
PostgreSQL
MinIO (object storage)
VNPAY Payment Gateway
Docker
MapStruct
Lombok

# Project Structure
src
├── config         -> Configuration classes (security, CORS, etc.)
├── controller     -> REST API controllers
├── dto            -> Data Transfer Objects
├── entity         -> JPA entity classes
├── mapper         -> MapStruct mappers
├── repository     -> Spring Data repositories
├── security       -> JWT and authentication logic
├── service        -> Business service layer

# Contact
Author: Xuan Vinh
GitHub: https://github.com/vinhrut
Email: nguyenxuanvinh2004tdt@gmail.com

