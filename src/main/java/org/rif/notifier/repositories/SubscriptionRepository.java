package org.rif.notifier.repositories;

import org.rif.notifier.models.entities.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, String> {
    List<Subscription> findByActive(int active);
}
