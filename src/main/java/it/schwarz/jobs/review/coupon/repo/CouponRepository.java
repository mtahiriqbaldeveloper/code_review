package it.schwarz.jobs.review.coupon.repo;

import it.schwarz.jobs.review.coupon.entities.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCode(String code);

    @Query("SELECT DISTINCT c FROM Coupon c LEFT JOIN FETCH c.applications")
    List<Coupon> findAllFetchingApplications();

    @Query("SELECT DISTINCT c FROM Coupon c LEFT JOIN FETCH c.applications WHERE c.code = :code")
    Optional<Coupon> findByCodeFetchingApplications(@Param("code") String code);
}
