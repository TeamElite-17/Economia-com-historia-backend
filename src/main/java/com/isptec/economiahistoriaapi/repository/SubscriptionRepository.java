package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, String> {
}
