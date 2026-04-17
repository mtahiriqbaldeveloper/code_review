package it.schwarz.jobs.review.coupon.controller;

import it.schwarz.jobs.review.coupon.dto.*;
import it.schwarz.jobs.review.coupon.service.CouponService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping
    public ResponseEntity<GetCouponsResponseDto> getCoupons() {
        return ResponseEntity.ok(GetCouponsResponseDto.of(couponService.findAllCoupons()));
    }

    @PostMapping
    public ResponseEntity<CreateCouponResponseDto> createCoupon(@Valid @RequestBody CreateCouponRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CreateCouponResponseDto.of(couponService.createCoupon(request.toCouponPayload())));
    }

    @GetMapping("/{couponCode}/applications")
    public ResponseEntity<GetCouponApplicationsResponseDto> getCouponApplications(@PathVariable String couponCode) {
        return ResponseEntity.ok(GetCouponApplicationsResponseDto.of(couponService.getApplications(couponCode)));
    }

    @PostMapping("/applications")
    public ResponseEntity<ApplyCouponResponseDto> applyCoupon(@Valid @RequestBody ApplyCouponRequestDto request) {
        return ResponseEntity.ok(ApplyCouponResponseDto.of(couponService.applyCoupon(request.basket().toBasket(), request.couponCode())));
    }
}
