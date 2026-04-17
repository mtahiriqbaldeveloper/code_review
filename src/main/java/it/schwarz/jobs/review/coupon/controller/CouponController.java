package it.schwarz.jobs.review.coupon.controller;

import it.schwarz.jobs.review.coupon.dto.*;
import it.schwarz.jobs.review.coupon.service.CouponService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping()
    public ResponseEntity<GetCouponsResponseDto> getCoupons() {
        var coupons = couponService.findAllCoupons();
        var response = GetCouponsResponseDto.of(coupons);
        return ResponseEntity.ok(response);
    }

    @PostMapping()
    public ResponseEntity<CreateCouponResponseDto> createCoupon(@Valid @RequestBody CreateCouponRequestDto request) {
        var coupon = request.toCouponPayload();
        var couponCreated = couponService.createCoupon(coupon);
        var response = CreateCouponResponseDto.of(couponCreated);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{couponCode}/applications")
    public ResponseEntity<GetCouponApplicationsResponseDto> getCouponApplications(@PathVariable String couponCode) {
        var couponApplications = couponService.getApplications(couponCode);
        var response = GetCouponApplicationsResponseDto.of(couponApplications);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/applications")
    public ResponseEntity<ApplyCouponResponseDto> applyCoupon(@Valid @RequestBody ApplyCouponRequestDto request) {
        var applicationResult = couponService.applyCoupon(request.basket().toBasket(), request.couponCode());
        var response = ApplyCouponResponseDto.of(applicationResult);
        return ResponseEntity.ok(response);
    }

}
