-- PostgreSQL: restore a known baseline (matches Liquibase dev/test seed) for isolated integration tests.
TRUNCATE TABLE coupon RESTART IDENTITY CASCADE;

INSERT INTO coupon (code, discount, min_basket_value, description) VALUES
    ('TEST_05_50', 5.00, 50.00, 'Get 5 Euro discount if you spend at least 50 Euro'),
    ('TEST_15_100', 15.00, 100.00, 'Get 15 Euro discount if you spend at least 100 Euro'),
    ('TEST_40_200', 40.00, 200.00, 'Get 40 Euro discount if you spend at least 200 Euro');
