package it.schwarz.jobs.review.coupon.entities;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "COUPON")
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "CODE", nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "DISCOUNT", nullable = false, precision = 10, scale = 2)
    private BigDecimal discount;

    @Column(name = "MIN_BASKET_VALUE", nullable = false, precision = 10, scale = 2)
    private BigDecimal minBasketValue;

    @Column(name = "DESCRIPTION", nullable = false, length = 1000)
    private String description;

    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applications = new ArrayList<>();

    public Coupon() {
    }

    public Coupon(String code, BigDecimal discount, String description, BigDecimal minBasketValue) {
        this.code = code;
        this.discount = discount;
        this.description = description;
        this.minBasketValue = minBasketValue;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public BigDecimal getMinBasketValue() {
        return minBasketValue;
    }

    public String getDescription() {
        return description;
    }

    public List<Application> getApplications() {
        return applications;
    }

    public void addApplication(Application application) {
        applications.add(application);
        application.setCoupon(this);
    }
}
