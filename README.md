
# User Management Application

A comprehensive Java 21 Spring Boot 3.5 Maven application for user management with CRUD operations, authentication, external API integration, and Kubernetes deployment support.

## Features

- **User Management CRUD**: Complete Create, Read, Update, Delete operations for users
- **Authentication Interceptor**: Token-based authentication for API endpoints
- **External API Integration**: Service to consume external REST APIs with configurable intervals
- **In-memory H2 Database**: Lightweight database for development and testing
- **Cucumber Tests**: Behavior-driven development testing framework
- **Kubernetes Deployment**: Complete K8s deployment configuration
- **Cron Job Integration**: Scheduled tasks for external API calls
- **Environment Configuration**: Configurable external endpoint intervals

## Technology Stack

- **Java 21**
- **Spring Boot 3.5**
- **Maven** for dependency management
- **H2 Database** (in-memory)
- **Spring Security** for authentication
- **Cucumber** for BDD testing
- **Docker** for containerization
- **Kubernetes** for orchestration

## Project Structure

```
user-management-app/
├── src/
│   ├── main/
│   │   ├── java/com/example/usermanagement/
│   │   │   ├── UserManagementApplication.java
│   │   │   ├── entity/User.java
│   │   │   ├── repository/UserRepository.java
│   │   │   ├── service/
│   │   │   │   ├── UserService.java
│   │   │   │   └── ExternalApiService.java
│   │   │   ├── controller/
│   │   │   │   ├── UserController.java
│   │   │   │   └── ExternalApiController.java
│   │   │   ├── interceptor/AuthenticationInterceptor.java
│   │   │   └── config/
│   │   │       ├── WebConfig.java
│   │   │       └── SecurityConfig.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       ├── java/com/example/usermanagement/cucumber/
│       └── resources/
│           ├── features/user_management.feature
│           └── application-test.yml
├── k8s/
│   ├── deployment.yaml
│   ├── service.yaml
│   ├── configmap.yaml
│   ├── cronjob.yaml
│   └── namespace.yaml
├── Dockerfile
├── pom.xml
└── README.md
```

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6+ (or use included Maven wrapper)
- Docker (for containerization)
- Kubernetes cluster (for deployment)

### Running Locally

1. **Clone and navigate to the project:**
   ```bash
   cd /home/ubuntu/user-management-app
   ```

2. **Build the application:**
   ```bash
   ./mvnw clean compile
   ```

3. **Run tests:**
   ```bash
   ./mvnw test
   ```

4. **Start the application:**
   ```bash
   ./mvnw spring-boot:run
   ```

5. **Access the application:**
   - API Base URL: `http://localhost:8080/api`
   - H2 Console: `http://localhost:8080/h2-console`
   - Health Check: `http://localhost:8080/actuator/health`

### API Endpoints

#### User Management
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/username/{username}` - Get user by username
- `GET /api/users/search?q={term}` - Search users
- `POST /api/users` - Create new user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user
- `PATCH /api/users/{id}/deactivate` - Deactivate user

#### External API (Mock)
- `GET /external-api/data` - Get fake external data
- `GET /external-api/health` - External API health check

### Authentication

All API endpoints (except external API and H2 console) require authentication. Include the following header:

```
Authorization: Bearer valid-token-123
```

### Example API Usage

1. **Create a user:**
   ```bash
   curl -X POST http://localhost:8080/api/users \
     -H "Authorization: Bearer valid-token-123" \
     -H "Content-Type: application/json" \
     -d '{
       "username": "johndoe",
       "email": "john@example.com",
       "password": "password123",
       "firstName": "John",
       "lastName": "Doe"
     }'
   ```

2. **Get all users:**
   ```bash
   curl -X GET http://localhost:8080/api/users \
     -H "Authorization: Bearer valid-token-123"
   ```

## Configuration

### Environment Variables

- `EXTERNAL_API_URL`: URL of the external API (default: `http://localhost:8080/external-api/data`)
- `EXTERNAL_API_INTERVAL`: Interval in milliseconds for calling external API (default: `60000`)

### Application Properties

Key configurations in `application.yml`:

```yaml
external:
  api:
    url: ${EXTERNAL_API_URL:http://localhost:8080/external-api/data}
    interval: ${EXTERNAL_API_INTERVAL:60000}
```

## Testing

### Running Cucumber Tests

```bash
./mvnw test
```

The Cucumber tests cover:
- User CRUD operations
- Authentication scenarios
- Search functionality
- User deactivation

### Test Reports

Cucumber reports are generated in `target/cucumber-reports/`

## Docker Deployment

### Build Docker Image

```bash
docker build -t user-management-app:latest .
```

### Run Docker Container

```bash
docker run -p 8080:8080 \
  -e EXTERNAL_API_URL=http://localhost:8080/external-api/data \
  -e EXTERNAL_API_INTERVAL=60000 \
  user-management-app:latest
```

## Kubernetes Deployment

### Deploy to Kubernetes

1. **Apply namespace (optional):**
   ```bash
   kubectl apply -f k8s/namespace.yaml
   ```

2. **Apply configurations:**
   ```bash
   kubectl apply -f k8s/configmap.yaml
   kubectl apply -f k8s/deployment.yaml
   kubectl apply -f k8s/service.yaml
   kubectl apply -f k8s/cronjob.yaml
   ```

3. **Check deployment status:**
   ```bash
   kubectl get pods
   kubectl get services
   ```

4. **Access the application:**
   ```bash
   # Port forward for local access
   kubectl port-forward service/user-management-service 8080:8080
   
   # Or use NodePort (if configured)
   # Access via http://<node-ip>:30080
   ```

### Kubernetes Components

- **Deployment**: Runs 2 replicas of the application
- **Service**: ClusterIP and NodePort services for internal/external access
- **ConfigMap**: Environment configuration
- **CronJob**: Scheduled external API calls every 5 minutes

## External API Integration

The application includes:

1. **ExternalApiService**: Service to call external APIs
2. **Scheduled Task**: Automatic periodic calls to external API
3. **Mock External API**: Fake endpoint for testing (`/external-api/data`)
4. **Configurable Interval**: Environment variable to control call frequency

## Database

### H2 Console Access

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

### Database Schema

The application automatically creates the following table:

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);
```

## Development

### Adding New Features

1. **Entity**: Add new entities in `entity/` package
2. **Repository**: Create repository interfaces in `repository/` package
3. **Service**: Implement business logic in `service/` package
4. **Controller**: Add REST endpoints in `controller/` package
5. **Tests**: Add Cucumber scenarios in `features/` directory

### Code Style

- Follow Spring Boot conventions
- Use proper validation annotations
- Implement proper error handling
- Add comprehensive tests

## Troubleshooting

### Common Issues

1. **Port already in use**: Change server port in `application.yml`
2. **Authentication failures**: Ensure correct Bearer token
3. **Database connection issues**: Check H2 configuration
4. **Kubernetes deployment issues**: Verify image availability and resource limits

### Logs

Check application logs:
```bash
# Local
./mvnw spring-boot:run

# Docker
docker logs <container-id>

# Kubernetes
kubectl logs <pod-name>
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Add tests for new functionality
4. Ensure all tests pass
5. Submit a pull request

## License

This project is licensed under the MIT License.
