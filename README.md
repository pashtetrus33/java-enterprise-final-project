# java_enterprise-final project

The project contains the following subprojects: 

1. Gateway-service
2. Discovery-service (combined with Config-service)
3. Auth-service
4. Order-service
5. Payment-service
6. Inventory-service
7. Delivery-service

## Environment

To run PostgreSQL with Kafka, you need to run the following command in the project root::
```
$sudo docker-compose up -d
```

Also, you have convenient [UI for Apache Kafka](https://github.com/provectus/kafka-ui) at URL:

http://localhost:9999/

Now you can run application services from your IDE in this order 
- Discovery
- Auth-service
- Order-service
- Payment-service
- Inventory-service
- Delivery-service
- Gateway-service

Afterwards on Gateway you can find joined Swagger UI at URL:

http://localhost:9090/swagger-ui.html


## Basic interactions

You can use those curl commands, or you can do all that with Swagger UI

### Authentication

To create user use this request to auth service: 
```bash
curl --location --request POST 'http://localhost:9090/auth-service/user/signup' \
--header 'Content-Type: application/json' \
--data '{
    "name": "user1",
    "password": "user1"
}'
```

After that you can get a token:
```bash
curl --location --request POST 'http://localhost:9090/auth-service/token/generate' \
--header 'Content-Type: application/json' \
--data '{
    "name": "user1",
    "password": "user1"
}'
```

Now you can use this token to authenticate requests to other service.

### Payment service

To create a balance you can send POST request:

```bash
curl --location --request POST 'http://localhost:9090/payment-service/balance' \
--header 'Authorization: Bearer <put token here>'
```

To replenish the user's balance you can send PATCH request:

```bash
curl --location --request PATCH 'http://localhost:9090/payment-service/balance/1' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <put token here>' \
--data '{
    "sum": 100000
}'
```

### Inventory service

To create an inventory you can send POST request:

```bash
curl --location --request POST 'http://localhost:9090/inventory-service/inventory' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <put token here>' \
--data '{
    "description": "Test inventory",
    "quantity": 22,
    "costPerItem": 10
}'
```

To replenish your inventory you can send PATCH request:

```bash
curl --location --request PATCH 'http://localhost:9090/inventory-service/inventory/1' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <put token here>' \
--data '{
    "quantity": 20
}'
```

### Order service

To create an order you can send POST request:

```bash
curl --location --request POST 'http://localhost:9090/order-service/order' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <put token here>' \
--data '{
  "orderDto":
    {
      "description": "Order for electronics delivery",
      "departureAddress": "123 Main Street, Springfield",
      "destinationAddress": "456 Elm Street, Shelbyville",
      "cost": 1500,
      "quantity": 2
    }

}'
```

You can change status of order with PATCH request:

```bash
curl --location --request PATCH 'http://localhost:9090/order-service/order/1' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <put token here>' \
--data '{
    "status": "REGISTERED",
    "serviceName": "ORDER_SERVICE",
    "comment": "Some comment to status"
}'
```

### Delivery service

To remove delivery you can send DELETE request:

```bash
curl --location --request DELETE 'http://localhost:9090/delivery-service/delivery/1' \
--header 'Authorization: Bearer <put token here>'
```