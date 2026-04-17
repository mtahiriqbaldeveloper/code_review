package it.schwarz.jobs.review.coupon.integration;

import it.schwarz.jobs.review.coupon.dto.*;
import it.schwarz.jobs.review.coupon.testdata.TestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
@Sql("/sql/reset-coupon-database.sql")
class CouponIntegrationTest extends AbstractPostgresIntegrationTest {

    private static final String COUPONS = "/api/coupons";
    private static final String APPLICATIONS = COUPONS + "/applications";

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void getCoupons_returnsSeededCoupons() {
        var response = restTemplate.getForObject(COUPONS, GetCouponsResponseDto.class);
        assertThat(response.coupons()).hasSize(3);
    }

    @Test
    void createCoupon_returnsCreatedCoupon() {
        var request = TestData.validCouponRequest();
        var response = restTemplate.postForObject(COUPONS, request, CreateCouponResponseDto.class);

        assertThat(response.coupon().code()).isEqualTo(request.code());
    }

    @Test
    void createCouponWhenDuplicateReturnsConflict() {
        var request = TestData.validCouponRequest();
        restTemplate.postForObject(COUPONS, request, CreateCouponResponseDto.class);

        var response = restTemplate.postForEntity(COUPONS, request, ErrorResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull()
                .extracting(ErrorResponseDto::message)
                .asString()
                .contains(request.code());
    }

    @Test
    void applyCouponReturnsDiscount() {
        var response = restTemplate.postForEntity(APPLICATIONS, TestData.validApplyRequest(), ApplyCouponResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull()
                .extracting(ApplyCouponResponseDto::appliedDiscount)
                .satisfies(discount -> assertThat(discount).isGreaterThan(BigDecimal.ZERO));
    }

    @Test
    void applyCouponUnknownCodeReturnsNotFound() {
        var response = restTemplate.postForEntity(APPLICATIONS, TestData.unknownCodeApplyRequest(), Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getCouponApplicationsAfterApplyHasOneEntry() {
        restTemplate.postForEntity(APPLICATIONS, TestData.validApplyRequest(), ApplyCouponResponseDto.class);

        ResponseEntity<GetCouponApplicationsResponseDto> response = restTemplate.getForEntity(
                COUPONS + "/" + TestData.SEEDED_COUPON_CODE + "/applications",
                GetCouponApplicationsResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull()
                .extracting(GetCouponApplicationsResponseDto::applicationTimestamps)
                .satisfies(timestamps -> assertThat(timestamps).hasSize(1));
    }

    @Test
    void getCouponApplicationsUnknownCouponReturnsNotFound() {
        var response = restTemplate.getForEntity(COUPONS + "/UNKNOWN/applications", Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
