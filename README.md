
## Cloning the Repository

To clone the repository and get started with the project, follow these steps:

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/grzegorzsz24/LAT-task.git
   ```
   Alternatively, you can download the repository as a ZIP file and extract it to your desired location.
2. **Navigate to the Project Directory**:
   ```bash
   cd LAT-task
   ```

## Setting up and Running the Application 

- **Prerequisites**:
  Ensure you have Java version 21 installed on your system to build and run this project.
  
- To build the project, use:
  ```bash
  ./mvnw clean install
  ```
- To run the application:
  ```bash
  ./mvnw spring-boot:run
  ```
  By default, the application is run on port 8080. If you need to change the port, modify the `application.yaml` file.
- To execute tests:
  ```bash
  ./mvnw test
  ```
- Alternatively, you can import the project into an Integrated Development Environment (IDE) like IntelliJ IDEA or Eclipse, and run it from there.

# API Endpoints

## ProductController
- **POST**: `http://localhost:8080/products`
- **GET**: `http://localhost:8080/products`
- **PATCH**: `http://localhost:8080/products/{id}`

## PromoCodeController
- **POST**: `http://localhost:8080/promo-codes`
- **GET**: `http://localhost:8080/promo-codes`
- **GET**: `http://localhost:8080/promo-codes/{code}`

## PurchaseController
- **GET**: `http://localhost:8080/purchases/discount-price`
- **POST**: `http://localhost:8080/purchases`

## SalesReportController
- **GET**: `http://localhost:8080/sales-report`

## Database Access
- **Database Console**: `http://localhost:8080/h2-console`
- **Credentials**:
  - **Username**: sa
  - **Password**: password

## API Documentation and Tools

- **Swagger UI**: Access the Swagger UI to explore and test the API endpoints at:
  [http://localhost:8080/swagger-ui/index.html#/](http://localhost:8080/swagger-ui/index.html#/)
  
- **Postman Collection**: A Postman collection is available within the project that can be imported to Postman for testing the API endpoints.

## Sample Queries for API Endpoints

### Product Operations

- **Create a New Product**:
  ```json
  POST http://localhost:8080/products
  {
    "name": "Keyboard",
    "description": "Wireless keyboard",
    "regularPrice": "49.99",
    "currency": "USD"
  }
  ```

- **Get All Products**:
  ```http
  GET http://localhost:8080/products
  ```

- **Update Product Data**:
  ```json
  PATCH http://localhost:8080/products/6
  {
    "name": "Premium keyboard"
  }
  ```

### Promo Code Operations

- **Create a New Promo Code**:
  ```json
  POST http://localhost:8080/promo-codes
  {
    "code": "1234567",
    "discountType": "FIXED",
    "expirationDate": "2024-05-20T20:00:00",
    "discountAmount": "100.00",
    "discountCurrency": "USD",
    "maxUsages": "1",
    "currentUsages": "0"
  }
  ```

- **Get All Promo Codes**:
  ```http
  GET http://localhost:8080/promo-codes
  ```

- **Get Promo Code's Details**:
  ```http
  GET http://localhost:8080/promo-codes/1234567
  ```

### Purchase Operations

- **Get Discount Price with a Promo Code**:
  ```http
  GET http://localhost:8080/purchases/discount-price?productId=6&promoCode=1234567
  ```

  productId=6&promoCode=1234567
  ```

- **Simulate a Purchase**:
  ```http
  POST http://localhost:8080/purchases
  {
    "productId": "6",
    "promoCode": "1234567"
  }
  ```

### Reporting Operations

- **Generate Sales Report**:
  ```http
  GET http://localhost:8080/sales-report
  ```
