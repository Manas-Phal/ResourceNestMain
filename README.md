# 📚 ResourceNest – Study Resource Manager

**ResourceNest** is a Java Swing-based desktop application designed to help students organize and manage their study resources efficiently in one place.  
It allows users to store and access **PDFs, Notes, Websites, and Video links**, while also providing filtering, bookmarking, graphical analytics, and smart study insights.

---

## ✨ Features

- ➕ Add new study resources
- ✏️ Edit existing resources
- ❌ Delete unwanted resources
- 🔗 Open resource links directly in browser
- 🔍 Search resources instantly
- 📂 Filter by difficulty (**Easy / Medium / Hard**)
- ⭐ Bookmark important resources
- 📊 View subject-wise and difficulty-wise graphs
- 💡 Smart study insights and recommendations
- 🎨 Clean modern UI with custom logo

---

## 🛠 Technologies Used

| Technology | Purpose |
|------------|---------|
| **Java Swing** | GUI Development |
| **Java AWT** | Graphics and Custom UI |
| **SQLite** | Database Storage |
| **JDBC** | Database Connectivity |
| **IntelliJ IDEA** | Development Environment |

---

## 📁 Project Structure

```bash
ResourceNest/
│
├── src/
│   └── main/
│       └── java/
│           └── org/example/models/
│               ├── ResourceManagerGUI.java
│               ├── DBConnection.java
│               └── CreateTable.java
│
├── src/main/resources/
│   └── images/
│       └── logo.png
│
├── resources.db
└── README.md
```

---

## ⚙️ Setup Instructions

### 1. Clone the repository

```bash
git clone <your-github-repository-link>
```

---

### 2. Open project in IntelliJ IDEA

Import the project and ensure **Java SDK** is configured.

---

### 3. Add SQLite JDBC Dependency

Add the SQLite JDBC `.jar` file to project libraries.

Example:

```xml
org.xerial:sqlite-jdbc
```

---

### 4. Create the database table

Run:

```bash
CreateTable.java
```

---

### 5. Start the application

Run:

```bash
ResourceManagerGUI.java
```

---

## 🗄 Database Schema

Table: **resources**

```sql
CREATE TABLE resources (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    subject TEXT,
    title TEXT,
    link TEXT,
    type TEXT,
    difficulty TEXT,
    bookmark INTEGER DEFAULT 0
);
```

---

## 📊 Application Screens

Include screenshots of:

- Main Dashboard <img width="940" height="484" alt="image" src="https://github.com/user-attachments/assets/7777d593-99ad-4d79-9694-9fa2313bfaaf" />


  
- Add Resource Form <img width="940" height="491" alt="image" src="https://github.com/user-attachments/assets/9d977d4f-3f9a-4fee-ab1e-7b4e7a6b7944" />
- Search Functionality
  <img width="940" height="207" alt="image" src="https://github.com/user-attachments/assets/f27b50b5-315d-4372-a50d-53712e4e764d" />
  <img width="940" height="209" alt="image" src="https://github.com/user-attachments/assets/57cb3358-d393-4dd5-a7b8-e8776080cf1c" />


- Subject Graph <img width="940" height="484" alt="image" src="https://github.com/user-attachments/assets/8acc677b-34a2-474f-9150-0225253aca1f" />


- Difficulty Graph <img width="940" height="491" alt="image" src="https://github.com/user-attachments/assets/414d0d0d-8c4d-4968-ace7-7a45be03a4d0" />

- Insights Popup <img width="485" height="440" alt="image" src="https://github.com/user-attachments/assets/46ee741d-9b49-4099-b9fc-002aad85316c" />


---

## 🚀 Future Improvements

- 🔐 Login / Signup system
- 🌙 Dark mode
- ☁️ Cloud sync
- 📄 Export resources to PDF
- 📱 Mobile version

---

