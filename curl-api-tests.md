# Coupon API curl test suite

This document contains a complete set of `curl` calls to validate expected coupon API behavior.

## Run all tests automatically

Use the provided script:

```bash
chmod +x ./test-coupons-api.sh
./test-coupons-api.sh
```

Optional custom URL:

```bash
./test-coupons-api.sh "localhost:8080"
```

or

```bash
./test-coupons-api.sh --base-url "localhost:8080"
```

The script auto-expands `localhost:8080` to `http://localhost:8080/api/coupons`.

## Manual curl checks

Examples use `http://localhost:8080/api/coupons` (change host, port, or scheme if your API differs).

### 1) Get coupons overview (expected `200`)

```bash
curl -i "http://localhost:8080/api/coupons"
```

### 2) Create coupon - valid (expected `201`)

```bash
curl -i -X POST "http://localhost:8080/api/coupons" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "CODE_12_20",
    "discount": 12.00,
    "minBasketValue": 20.00,
    "description": "12 for 20"
  }'
```

### 3) Create coupon - duplicate code (expected `409`)

```bash
curl -i -X POST "http://localhost:8080/api/coupons" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "CODE_12_20",
    "discount": 12.00,
    "minBasketValue": 20.00,
    "description": "duplicate should fail"
  }'
```

### 4) Create coupon - validation failure (negative discount, expected `400`)

```bash
curl -i -X POST "http://localhost:8080/api/coupons" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "BAD_NEG",
    "discount": -12.00,
    "minBasketValue": 20.00,
    "description": "invalid"
  }'
```

### 5) Create coupon - business rule failure (zero discount, expected `422`)

```bash
curl -i -X POST "http://localhost:8080/api/coupons" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "ZERO_DISC",
    "discount": 0.00,
    "minBasketValue": 20.00,
    "description": "zero discount should fail business rule"
  }'
```

### 6) Apply coupon - valid (expected `200`)

```bash
curl -i -X POST "http://localhost:8080/api/coupons/applications" \
  -H "Content-Type: application/json" \
  -d '{
    "basket": { "value": 60.00 },
    "couponCode": "TEST_05_50"
  }'
```

### 7) Apply coupon - coupon not found (expected `404`)

```bash
curl -i -X POST "http://localhost:8080/api/coupons/applications" \
  -H "Content-Type: application/json" \
  -d '{
    "basket": { "value": 60.00 },
    "couponCode": "<NOT-EXISTING-CODE>"
  }'
```

### 8) Apply coupon - basket too low (expected `422`)

```bash
curl -i -X POST "http://localhost:8080/api/coupons/applications" \
  -H "Content-Type: application/json" \
  -d '{
    "basket": { "value": 1.00 },
    "couponCode": "TEST_05_50"
  }'
```

### 9) Get coupon applications - valid coupon code (expected `200`)

```bash
curl -i "http://localhost:8080/api/coupons/TEST_05_50/applications"
```

### 10) Get coupon applications - invalid coupon code (expected `404`)

```bash
curl -i "http://localhost:8080/api/coupons/NO_SUCH_CODE/applications"
```
