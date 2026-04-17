package it.schwarz.jobs.review.coupon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.schwarz.jobs.review.coupon.service.CouponService;
import it.schwarz.jobs.review.coupon.testdata.TestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class CouponControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper json;
    @MockBean
    private CouponService couponService;

    private static final String API = "/api/coupons";

    @Test
    void getCouponsReturnsOk() throws Exception {
        when(couponService.findAllCoupons()).thenReturn(List.of());

        mockMvc.perform(get(API))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coupons").isArray());
    }

    @Test
    void createCouponValidReturnsCreated() throws Exception {
        when(couponService.createCoupon(any())).thenReturn(TestData.coupon12For20());

        mockMvc.perform(post(API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(TestData.validCouponRequest())))
                .andExpect(status().isCreated());
    }

    @Test
    void createCouponInvalidReturnsBadRequest() throws Exception {
        mockMvc.perform(post(API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(TestData.invalidCouponRequest())))
                .andExpect(status().isBadRequest());
    }
}
