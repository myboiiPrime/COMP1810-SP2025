

1. Start Backend (Java Spring Boot)
```bash
cd java-backend
mvn spring-boot:run
```
Backend will run on: http://localhost:8080

2. Start Frontend (Vue.js)
```bash
cd frontend
npm install
npm run dev
```
Frontend will run on: http://localhost:5173


 Technology Stack

**Frontend:**
- Vue.js 3 with Vue Router
- Bootstrap 5 for styling
- Axios for API calls

**Backend:**
- Java Spring Boot
- Spring Data JPA
- H2 Database (in-memory)
- RESTful APIs

Features

- **Book Management**: Add, edit, delete, and search books
- **Shopping Cart**: Add books to cart and checkout
- **User Management**: Customer registration and profiles
- **Order Processing**: Complete order workflow
- **Admin Dashboard**: System monitoring and management

Project Structure

```
SP2025/
├── frontend/          # Vue.js frontend application
│   ├── src/
│   │   ├── views/     # Page components
│   │   ├── router/    # Vue Router configuration
│   │   └── services/  # API service calls
│   └── package.json
├── java-backend/      # Spring Boot backend
│   ├── src/main/java/ # Java source code
│   └── pom.xml        # Maven dependencies
└── README.md
```

**Required Software:**
- Java 17 or higher
- Node.js 18 or higher
- Maven 3.6 or higher
- Git

 Troubleshooting

- Make sure Java 17+ is installed
- Check if port 8080 is available
- Run `mvn clean install` first

- Make sure Node.js 18+ is installed
- Check if port 5173 is available


Performance Analysis

Complexity Analysis

The system includes comprehensive performance analysis tools:

Time Complexity
- **Hash Search**: O(1) average case
- **Binary Search**: O(log n)
- **Linear Search**: O(n)
- **Merge Sort**: O(n log n)
- **Stack Operations**: O(1)
- **Priority Queue**: O(log n) for insert/delete

Space Complexity
- **Hash Tables**: O(n)
- **Merge Sort**: O(n)
- **Binary Search**: O(1)
- **Data Structures**: O(n)