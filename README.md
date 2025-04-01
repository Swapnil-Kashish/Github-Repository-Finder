# GitHub Repository Finder

## 📌 Project Overview

**GitHub Repository Finder** A Spring Boot application that fetches repository details from the GitHub REST API, stores them in PostgreSQL, and provides APIs to retrieve stored results with filtering options. 

## Features ✅

 -   🔍 Search GitHub repositories by query, language, and sorting.
 -   💾 Store repository details in PostgreSQL.
 -    📊 Retrieve repositories with filters (stars, forks, etc.).
 -   ⚡ RESTful APIs using Spring Boot.
 -   🔄 Efficient database operations with JPA.
 -   🛠️ Unit tests using JUnit & Mockito.
 -   🔒 Secure API calls using GitHub token authentication. 

---

## 🚀 How to Start the Project

### 📌 Prerequisites
- Java 17+  
- Maven  
- PostgreSQL (or H2 for testing)  

### 🔧 Steps to Run

```sh
1. Clone the repository  
   git clone https://github.com/your-username/github-repo-finder.git
   cd github-repo-finder 
```
2. Configure the database in `application.properties`  
```sh
   github.api.url=https://api.github.com/search/repositories
   github.api.token=your-github-token
   spring.datasource.url=jdbc:postgresql://localhost:5432/github_db
   spring.datasource.username=your-db-username
   spring.datasource.password=your-db-password 
```
3. Run the application
```sh
   mvn spring-boot:run  
```
5. The server starts on
```sh
   http://localhost:9096
```


# 📌 API Documentation

1️⃣ Insert Repositories from GitHub
   - Method: POST  
   - URL: http://localhost:9096/api/github/search?query=springboot
   - Description: Find Repositories on github and store them in DB
   - Request Body (JSON):
```sh
     {
      "query": "spring boot",
      "language": "Java",
      "sort": "stars"
  }
```
Response (JSON):
```sh
    {
      "recordId":1,
      "message":"Record added successfully",
      "dataset":"employee_dataset"
    }
```

2️⃣ Fetch records sorted by stars  
   - Method: GET  
   - URL: http://localhost:9096/api/github/repositories?language=Java&minStars=3000&sort=stars
   - Description: Groups the records by department.
   - Response (JSON):
```sh  
[
  {
    "id": 1,
    "name": "repo1",
    "language": "Java",
    "stars": 3000
  },
  {
    "id": 2,
    "name": "repo2",
    "language": "Java",
    "stars": 4000
  }
]

```

## Access the Application:
- Once the application is running, test it through postman at http://localhost:9096/API_END_POINTS_URL (or the appropriate port if configured differently).
- Modify `application.properties` database configurations as needed.

## Contributing:
We welcome contributions from the community to further improve and enhance the Application. If you'd like to contribute, please fork the repository, make your changes, and submit a pull request.
