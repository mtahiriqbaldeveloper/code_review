# Code Review Findings

## Findings

### CR-01 Discount not subtracted when applying coupon

- **Location:** `CouponUseCases#applyCoupon`
- **Issue:** Validation passes, but the returned basket value remains unchanged after coupon application.
- **Impact:** Incorrect business behavior; users do not receive the expected discount.
- **Suggestion:** Return a basket using discounted value:
  - `AmountOfMoney discountedPrice = basketValue.subtract(couponToApply.getDiscount());`
  - `return new ApplicationResult(new Basket(discountedPrice), couponToApply);`

### CR-02 Coupon not found mapped to wrong HTTP status

- **Location:** Exception handler for `CouponCodeNotFoundException`
- **Issue:** Missing coupon is mapped to `400 Bad Request`.
- **Impact:** API contract ambiguity; clients cannot distinguish invalid request shape from missing resource.
- **Suggestion:** Map to `404 Not Found`.

### CR-03 Invalid JPA relationship mapping (`mappedBy`)

- **Location:** Coupon/Application entity association
- **Issue:** `@OneToMany(mappedBy = "...")` targets a non-entity association field (`couponCode` string style mapping).
- **Impact:** Broken relation navigation, weak referential consistency, drift between coupon and application data.
- **Suggestion:** Use real FK relationship:
  - On application entity: `@ManyToOne(fetch = LAZY) @JoinColumn(name = "coupon_id", nullable = false) private CouponJpaEntity coupon;`
  - On coupon entity: `@OneToMany(mappedBy = "coupon", cascade = ALL, orphanRemoval = true)`.

### CR-04 Repository ID type mismatch

- **Location:** `ApplicationJpaRepository`
- **Issue:** Entity primary key type is `Long`, repository declared with `String`.
- **Impact:** Type safety and runtime behavior risks in repository operations.
- **Suggestion:** Align declaration: `JpaRepository<ApplicationJpaEntity, Long>`.

### CR-05 Validation allows whitespace-only text fields

- **Location:** request DTO validation on coupon text fields
- **Issue:** Using `@NotNull` + `@Size` permits values like `"  "`.
- **Impact:** Invalid domain data can pass API validation.
- **Suggestion:** Use `@NotBlank` (with `@Size`) for `code`/`description` style fields.

### CR-06 Lazy loading at detached boundary (`LazyInitializationException` risk)

- **Location:** `JpaCouponProvider#findAll`, `getCouponApplications`
- **Issue:** Methods access lazy collections (`getApplications()`) after repository call returns and session/transaction may be closed.
- **Impact:** Runtime exceptions in non-OSIV-safe paths; unpredictable behavior across environments.
- **Suggestion:** Prefer one of:
  - Transactional read boundary around full mapping (`@Transactional(readOnly = true)`), and/or
  - Explicit fetch strategies (`join fetch`, DTO projection query) to avoid lazy access outside persistence context.


### CR-07 Controller/service code verbosity

- **Location:** controller mapping methods and straightforward pass-through sections
- **Issue:** Temporary variables and comments add noise without changing behavior.
- **Impact:** Lower readability and slower reviews.
- **Suggestion:** Keep direct transformations concise; remove redundant comments/temps where intent is obvious.


### CR-08 Domain value object hardening opportunity (`AmountOfMoney`)

- **Location:** money value object creation/comparison/subtraction paths
- **Issue:** Invariant checks can be strengthened for null and negative-result safety.
- **Impact:** Potential invalid money states or late failures.
- **Suggestion:** Enforce:
  - `Objects.requireNonNull` on constructors/factories/ops,
  - non-negative invariant in constructor/factory,
  - subtraction guard against negative result with explicit error.

### CR-09 Structural maintainability improvements

- **Location:** package and class organization
- **Issue:** Mixed concerns across layers reduce discoverability.
- **Impact:** Harder onboarding and maintenance.
- **Suggestion:** Keep clear boundaries (`controller`, `dto`, `service`, `repo`, `entities`, `exception`) and use conventional Spring naming (`*Controller`, `*Service`, `*Repository`).

### CR-10 Candidate immutable domain types can be converted to records

- **Location:** `ApplicationResult`, `Basket`, `Coupon`, `CouponApplications`
- **Issue:** Data carrier classes appear primarily immutable and may contain boilerplate constructors/accessors.
- **Impact:** Extra verbosity and higher maintenance effort for simple value-style types.
- **Suggestion:** Consider Java `record` where invariants and framework constraints allow it. Example:

```java
public record Coupon(String code, AmountOfMoney discount, AmountOfMoney minBasketValue, String description,
                     long applicationCount) {

    public Coupon(String code, AmountOfMoney discount, AmountOfMoney minBasketValue, String description) {
        this(code, discount, minBasketValue, description, 0);
    }
}
```

### CR-11 Coupon/Application entity remodeling and mapping integrity

- **Location:** `Coupon` and `Application` persistence entities
- **Bug:** `Coupon` used `@OneToMany(mappedBy = "couponCode")` while `Application` only had a string `COUPON_CODE`, not a JPA association, so the mapping was invalid and the DB could not enforce a real parent-child link.
- **Risk:** Coupon `code` can change in `COUPON` while `APPLICATION` still holds the old string, orphaning history and breaking consistency.
- **Fix:** Give `Coupon` a surrogate primary key (`id`) and keep `code` unique; on `Application`, use `@ManyToOne` + `COUPON_ID` FK with `@OneToMany(mappedBy = "coupon", cascade = ALL, orphanRemoval = true)`.
- **Code:** Repositories resolve coupons by `code` where the API needs it, use fetch joins for list/timestamp reads, and register applications by attaching to the loaded `Coupon` entity so Hibernate persists FK relations correctly.
- **Data:** `data.sql` seeds applications via `COUPON_ID` subqueries instead of duplicate coupon strings.


## Suggested HTTP Error Mapping

Implemented in `CouponExceptionHandler` as follows:

- `MethodArgumentNotValidException` (failed `@Valid` on request body) -> `400 Bad Request` (response includes field errors)
- `CouponCodeNotFoundException` -> `404 Not Found`
- `CouponAlreadyExistsException` -> `409 Conflict`
- `DataIntegrityViolationException` -> `409 Conflict` (any DB integrity violation, not only duplicates)
- `CouponNotValidException` -> `422 Unprocessable Entity`
- `BasketValueTooLowException` -> `422 Unprocessable Entity`
- `BusinessException` (no more specific handler) -> `400 Bad Request` — in this codebase every `BusinessException` subclass is covered above, so plain `BusinessException` or a new subclass without its own handler would use this
- any other unhandled `Exception` -> `500 Internal Server Error`

### CR-12 Replace Hibernate DDL auto with Liquibase migrations

- **Location:** `application-prd.properties`, schema management
- **Issue:** Production used `spring.jpa.hibernate.ddl-auto=update`, letting Hibernate modify schema automatically.
- **Impact:** No version control, no rollback capability, no review process, potential data loss on concurrent startups.
- **Fix:** Added Liquibase for versioned schema migrations:
  - `spring.jpa.hibernate.ddl-auto=none` (common), `validate` (prd) - Hibernate never modifies schema
  - Schema defined in `db/changelog/changes/001-initial-schema.yaml` (COUPON + APPLICATION tables with FK)
  - Dev seed data in `002-dev-seed-data.yaml` with `context: dev` - only runs in dev profile
  - H2 for dev profile, PostgreSQL for prd profile (both drivers available, profile selects datasource)
  - Removed `data.sql` - seed data now managed by Liquibase contexts
- **Benefit:** Auditable migrations, safe rollbacks, team collaboration, CI/CD friendly.

---

## Test Suite Improvements

Integration tests now run against a **PostgreSQL** container (`AbstractPostgresIntegrationTest`, `@ServiceConnection`, Liquibase `test` context in `application-integration-test.properties`) so behavior matches production better than H2-only runs. **`@Sql`** resets seed data before each test, replacing **`@Order`** and shared mutable state. Tests live under **`it.schwarz.jobs.review.coupon`** mirroring main code (`common`, `controller`, `integration`, `service`, `testdata`). Naming is behavior-oriented (no `test` prefix); fixtures are a single **`TestData`** with flat static helpers. Redundant **`CouponAppTests`** and extra fixture classes were dropped. **`CouponServiceTest`** uses **`@BeforeEach`** for shared mocks.

| File | Role |
|------|------|
| `CouponIntegrationTest` | HTTP tests via Testcontainers Postgres |
| `CouponControllerTest` | `MockMvc` with mocked service |
| `CouponServiceTest` | Service unit tests |
| `AmountOfMoneyTest` | Value object tests |
| `AbstractPostgresIntegrationTest` | Shared container setup |
| `ErrorResponseDto` | Parse error JSON in tests |
| `TestData` | Shared fixtures |

## Future improvements

- **OpenAPI / Swagger:** Add SpringDoc OpenAPI (or similar) so the coupon API is documented interactively and can be tried from the browser. For now, manual checks are covered by **`curl-api-tests.md`** and **`test-coupons-api.sh`**.
- **Pageable coupon list:** The endpoint that returns all coupons and applications should support **pagination** (and 
  optional sorting/filtering) so clients do not pull an unbounded list and stress the API or database as the catalog grows.
- **Caching and database indexing:** Use **indexes** on columns used in lookups and joins (e.g. coupon `code`, foreign keys on `APPLICATION`) so queries stay fast as data grows. Add **caching** (e.g. Spring Cache on hot read paths like coupon-by-code or list summaries) where staleness is acceptable, with clear eviction or TTL when coupons change.
- **API versioning:** Introduce a **versioned base path** (e.g. `/api/v1/coupons`) or header-based negotiation so breaking changes can ship without silently breaking existing clients. That improves **scalability** (roll out new behavior behind a new version) and **maintainability** (deprecate old versions on a clear timeline).
- **Admin panel:** Provide a small **admin UI** (or internal tool) so operators can perform **CRUD** on coupons, inspect usage, and disable or fix bad data without direct database access or raw API calls. Protect it with **authentication and authorization** (admin-only roles).
- **Structured logging and observability:** Add **structured logs** (e.g. JSON with correlation/request IDs) for key flows—coupon creation, apply, conflicts, and errors—so operators can trace requests and spot anomalies. Combine with **metrics** (latency, error rates, apply volume) and **health checks** (`/actuator/health` or similar) to monitor overall service health and tune capacity.
- **Richer coupon and application model:**
  - **Coupon:** Model **coupon kinds** (e.g. one-time use vs reusable), **expiry**, and **validity windows** (valid from / valid to). Validation rules when applying a coupon would enforce these constraints alongside min basket and discount rules.
  - **Application:** Add an **`orderId`** (or equivalent business key) for the order on which the coupon was applied. That supports **returns and refunds**: when an order is cancelled or returned, the system can identify the application and optionally **restore** coupon eligibility (e.g. release a one-time-use coupon back to the customer).

