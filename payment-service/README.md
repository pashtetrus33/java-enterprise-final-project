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
    "sum": 9999999
}'
```