# 🎓 Wagas - Scholarship Management System

<div align="center">

[![Laravel](https://img.shields.io/badge/Laravel-FF2D20?style=for-the-badge&logo=laravel&logoColor=white)](https://laravel.com)
[![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://mysql.com)
[![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=Postman&logoColor=white)](https://postman.com)
[![PHP](https://img.shields.io/badge/PHP-777BB4?style=for-the-badge&logo=php&logoColor=white)](https://php.net)

**A comprehensive web application for managing scholarship programs, students, and user accounts**

</div>

---

## 👨‍💻 Developer

<table>
  <tr>
    <td align="center">
      <img src="https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png" width="100" height="100" style="border-radius: 50%;"/>
      <br />
      <strong>Jim Cyron R. Wagas</strong>
      <br />
      <em>BSIT - 2D</em>
      <br />
      <em>St. Cecilia's College-Cebu, Inc.</em>
    </td>
  </tr>
</table>

---

## 📋 Table of Contents

- [Project Feature List](#-project-feature-list)
- [Tech Stack](#-tech-stack)
- [Prerequisites](#-prerequisites)
- [Installation Guide](#-installation-guide)
- [API Endpoints](#-api-endpoints)
- [Database Structure](#-database-structure)
- [License](#-license)

---

## 📝 Project Feature List

### 1.1 Main Feature: Authentication
| Action | Description |
| :----- | :---------- |
| 1.1.1 Register Account | Create a new user account |
| 1.1.2 Login | Authenticate and get access token |
| 1.1.3 Logout | Invalidate current session |
| 1.1.4 Get Logged In User | Retrieve authenticated user info |

---

### 1.2 Main Feature: User Management

#### 1.2.1 Sub-Feature: Admin Account Management
| Action | Description |
| :----- | :---------- |
| 1.2.1.1 Add User | Create a new user account |
| 1.2.1.2 Edit User | Update user information |
| 1.2.1.3 Delete User | Remove a user account |
| 1.2.1.4 View User List | Display all user accounts |
| 1.2.1.5 View User by ID | Display a specific user |

#### 1.2.2 Sub-Feature: Student Account Management
| Action | Description |
| :----- | :---------- |
| 1.2.2.1 Add Student | Enroll a new student |
| 1.2.2.2 Edit Student | Update student information |
| 1.2.2.3 Delete Student | Remove a student record |
| 1.2.2.4 View Student List | Display all students |
| 1.2.2.5 View Student by ID | Display a specific student |

---

### 1.3 Main Feature: Scholarship Management

#### 1.3.1 Sub-Feature: Scholarship Programs
| Action | Description |
| :----- | :---------- |
| 1.3.1.1 Add Scholarship | Create a new scholarship program |
| 1.3.1.2 Edit Scholarship | Update scholarship details |
| 1.3.1.3 Delete Scholarship | Remove a scholarship program |
| 1.3.1.4 View Scholarship List | Display all scholarships |
| 1.3.1.5 View Scholarship by ID | Display a specific scholarship |

#### 1.3.2 Sub-Feature: Scholarship Requirements
| Action | Description |
| :----- | :---------- |
| 1.3.2.1 Add Requirement | Add a document requirement to a scholarship |
| 1.3.2.2 Edit Requirement | Update requirement details |
| 1.3.2.3 Delete Requirement | Remove a requirement |
| 1.3.2.4 View Requirements | Display all requirements |
| 1.3.2.5 View Requirement by ID | Display a specific requirement |

---

### 1.4 Main Feature: Application Management

#### 1.4.1 Sub-Feature: Scholarship Applicants
| Action | Description |
| :----- | :---------- |
| 1.4.1.1 Add Applicant | Register a new scholarship applicant |
| 1.4.1.2 Edit Applicant | Update applicant information |
| 1.4.1.3 Delete Applicant | Remove an applicant record |
| 1.4.1.4 View Applicant List | Display all applicants |
| 1.4.1.5 View Applicant by ID | Display a specific applicant |

#### 1.4.2 Sub-Feature: Scholarship Applications
| Action | Description |
| :----- | :---------- |
| 1.4.2.1 Submit Application | Apply for a scholarship program |
| 1.4.2.2 Edit Application | Update application status or remarks |
| 1.4.2.3 Delete Application | Remove a submitted application |
| 1.4.2.4 View Application List | Display all applications |
| 1.4.2.5 View Application by ID | Display a specific application |

#### 1.4.3 Sub-Feature: Documents
| Action | Description |
| :----- | :---------- |
| 1.4.3.1 Upload Document | Attach a document to an application |
| 1.4.3.2 Update Document | Replace or update document details |
| 1.4.3.3 Delete Document | Remove an uploaded document |
| 1.4.3.4 View Document List | Display all documents |
| 1.4.3.5 View Document by ID | Display a specific document |

---

### 1.5 Main Feature: Reports
| Action | Description |
| :----- | :---------- |
| 1.5.1 Applicant Report | Summary of applicants grouped by status |
| 1.5.2 Scholarship Report | Scholarship slot usage and application counts |
| 1.5.3 Approved Scholars Report | List of all approved scholars |

---

## 🛠 Tech Stack

| Technology | Purpose |
| :--------- | :------ |
| **Laravel 10** | Backend API Framework |
| **MySQL** | Relational Database |
| **Laravel Sanctum** | API Token Authentication |
| **PHP 8.1** | Server-side Language |
| **Postman** | API Testing |
| **XAMPP** | Local Development Server |

---

## ⚙️ Prerequisites

| Tool | Purpose | Status |
| :--- | :--- | :---: |
| **XAMPP** | Local Server & MySQL Database | ✅ Required |
| **VS Code** | Primary Code Editor | ✅ Recommended |
| **Composer** | PHP Dependency Manager | ✅ Required |
| **Postman** | API Testing & Documentation | ✅ Required |

---

## 🚀 Installation Guide

### Step 1 — Clone the repository
```bash
git clone https://github.com/your-username/wagas-scholarship.git
cd wagas-scholarship
```

### Step 2 — Install PHP dependencies
```bash
composer install
```

### Step 3 — Configure environment
```bash
cp .env.example .env
php artisan key:generate
```

### Step 4 — Set up your database
Open `.env` and update:
```env
DB_DATABASE=Wagas
DB_USERNAME=root
DB_PASSWORD=
```

### Step 5 — Run migrations
```bash
php artisan migrate
```

### Step 6 — Create storage link
```bash
php artisan storage:link
```

### Step 7 — Start the server
```bash
php artisan serve
```

The API is now running at `http://localhost:8000`

> **Default Admin Account:** `admin@gmail.com` / `password`

---

## 📡 API Endpoints

> **📌 Postman:**

---

###   Auth

| Method | Endpoint | Description | Auth |
| :----- | :------- | :---------- | :--- |
| `POST` | `/api/login` | Login and get token | Public |
| `POST` | `/api/register` | Register new account | Public |
| `GET`  | `/api/user` | Get logged in user | Required |
| `POST` | `/api/logout` | Logout current session | Required |

---

### 🎓 Students

| Method | Endpoint | Description |
| :----- | :------- | :---------- |
| `GET`    | `/api/students` | Get all students |
| `POST`   | `/api/students` | Create new student |
| `GET`    | `/api/students/{id}` | Get student by ID |
| `PUT`    | `/api/students/{id}` | Update student |
| `DELETE` | `/api/students/{id}` | Delete student |

---

### 💰 Scholarships

| Method | Endpoint | Description |
| :----- | :------- | :---------- |
| `GET`    | `/api/scholarships` | Get all scholarships |
| `POST`   | `/api/scholarships` | Create new scholarship |
| `GET`    | `/api/scholarships/{id}` | Get scholarship by ID |
| `PUT`    | `/api/scholarships/{id}` | Update scholarship |
| `DELETE` | `/api/scholarships/{id}` | Delete scholarship |

---

### 📋 Requirements

| Method | Endpoint | Description |
| :----- | :------- | :---------- |
| `GET`    | `/api/requirements` | Get all requirements |
| `POST`   | `/api/requirements` | Create new requirement |
| `GET`    | `/api/requirements/{id}` | Get requirement by ID |
| `PUT`    | `/api/requirements/{id}` | Update requirement |
| `DELETE` | `/api/requirements/{id}` | Delete requirement |

---

### 👤 Scholarship Applicants

| Method | Endpoint | Description |
| :----- | :------- | :---------- |
| `GET`    | `/api/applicants` | Get all applicants |
| `POST`   | `/api/applicants` | Create new applicant |
| `GET`    | `/api/applicants/{id}` | Get applicant by ID |
| `PUT`    | `/api/applicants/{id}` | Update applicant |
| `DELETE` | `/api/applicants/{id}` | Delete applicant |

---

### 📄 Scholarship Applications

| Method | Endpoint | Description |
| :----- | :------- | :---------- |
| `GET`    | `/api/applications` | Get all applications |
| `POST`   | `/api/applications` | Submit new application |
| `GET`    | `/api/applications/{id}` | Get application by ID |
| `PUT`    | `/api/applications/{id}` | Update application status |
| `DELETE` | `/api/applications/{id}` | Delete application |

---

### 📁 Documents

| Method | Endpoint | Description |
| :----- | :------- | :---------- |
| `GET`    | `/api/documents` | Get all documents |
| `POST`   | `/api/documents` | Upload document (form-data) |
| `GET`    | `/api/documents/{id}` | Get document by ID |
| `PUT`    | `/api/documents/{id}` | Update document |
| `DELETE` | `/api/documents/{id}` | Delete document |

---

### 👥 Users

| Method | Endpoint | Description |
| :----- | :------- | :---------- |
| `GET`    | `/api/users` | Get all users |
| `POST`   | `/api/users` | Create new user |
| `GET`    | `/api/users/{id}` | Get user by ID |
| `PUT`    | `/api/users/{id}` | Update user |
| `DELETE` | `/api/users/{id}` | Delete user |

---

### 📊 Reports

| Method | Endpoint | Description |
| :----- | :------- | :---------- |
| `GET` | `/api/reports/applicants` | Applicant summary report |
| `GET` | `/api/reports/scholarships` | Scholarship slot usage report |
| `GET` | `/api/reports/approved` | Approved scholars report |

---

## 🗄 Database Structure

| Table | Description |
| :---- | :---------- |
| `roles` | User roles (Admin, Principal, Teacher) |
| `user_statuses` | Account statuses (Active, Inactive) |
| `users` | System user accounts |
| `year_levels` | Student year levels (1st - 4th Year) |
| `courses` | Available courses (BS IT, BS CS) |
| `sections` | Class sections (A, B, C, D) |
| `students` | Student records |
| `scholarships` | Scholarship programs |
| `requirements` | Document requirements per scholarship |
| `applicants` | Scholarship applicants |
| `applications` | Scholarship applications |
| `documents` | Uploaded applicant documents |
| `personal_access_tokens` | Sanctum API tokens |

---

## 📜 License

This project is developed for academic purposes at **St. Cecilia's College-Cebu, Inc.**

© 2026 Jim Cyron R. Wagas — BSIT 2-D
