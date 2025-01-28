# **Short URL Project** 🌐🔗

This project allows you to create and access short URLs through a simple API powered by AWS Lambda functions. The project includes two key functions: **`shortUrl`** for generating short URLs and **`shortUrlRedirect`** for redirecting users to the original URL.

---

## **Features** 🚀

- **Short URL Creation**: Create a unique short URL by providing an original URL.
- **URL Redirection**: Redirect users to the original URL when they access the short URL.
- **AWS Lambda Integration**: The URL shortening and redirection logic is handled by AWS Lambda functions, ensuring high scalability and performance.
- **API Gateway**: Exposes the Lambda functions through a RESTful API, making them accessible via HTTP requests.
- **AWS S3**: The original URL and the expiration time are stored on AWS S3.
---

## **How It Works** 🛠️

### **1. Short URL Creation (`shortUrl`)**
- The `shortUrl` function generates a short URL by creating a unique code.
- The original URL is stored in an AWS S3 bucket, using the generated code as a reference.
- The short URL is then returned to the user.

**API Endpoint**: `POST /create`
- **Request Body**:
  ```json
  {
    "originalUrl": "https://www.example.com",
    "expirationTime": 3432954
  }
   ```
- **Response**:
  ```json
  {
    "code": "KD349" 
  }
   ```

### **2. URL Redirection (`shortUrlRedirect`)** 

The `shortUrlRedirect` function retrieves the original URL from the S3 bucket using the provided unique code. The user is then redirected to the original URL if the expiration time is greater than the current time.

- **API Endpoint**: `GET /{code}`
  
## **Diagram of the Flow** 📊

Below is a diagram showing the flow of the Short URL creation and redirection process:

![diagram flow](./shortUrlDiagram.png)
