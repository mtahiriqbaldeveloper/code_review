# Schwarz IT Coupon-Service

### Overview

This is a small and simple coupon service. Intentionally, it supports only a minimal feature set. This comprises:

* Maintaining coupons (Only create is implemented so far).
* Getting an overview of existing coupons and how often they have been attempt to apply.
* Applying of a coupon to a given basket to test if an application would be valid.
* Getting a detailed overview of all successful attempts to apply a specific coupon.


### Compile 

You need JDK 21 (or later) and optionally [Apache Maven](https://maven.apache.org/) to compile and run the code.

#### Compile Option 1: with Maven Wrapper (no need to install Maven)
`./mvnw clean package`

#### Compile Option 2: with Maven installed on your machine
`mvn clean package`

### Run 
To run the application you should set the according profile of the environment.

#### IDE
Set the profile to "dev" in the settings of your IDE.

#### Development Environment
`java -jar -Dspring.profiles.active=dev ./target/coupon-0.0.1-SNAPSHOT.jar`

#### Production Environment
Start the PostgreSQL container first:
```bash
docker-compose up -d
```

Then run the application:
```bash
java -jar -Dspring.profiles.active=prd ./target/coupon-0.0.1-SNAPSHOT.jar
```

