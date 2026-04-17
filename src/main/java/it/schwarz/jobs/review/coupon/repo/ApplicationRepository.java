package it.schwarz.jobs.review.coupon.repo;

import it.schwarz.jobs.review.coupon.entities.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

}
