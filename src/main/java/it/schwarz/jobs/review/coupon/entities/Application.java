package it.schwarz.jobs.review.coupon.entities;


import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "APPLICATION")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COUPON_ID", nullable = false)
    private Coupon coupon;

    @Column(name = "TIMESTAMP", nullable = false)
    private Instant timestamp;


    public Application() {
    }

    public Application(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public Coupon getCoupon() {
        return coupon;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
