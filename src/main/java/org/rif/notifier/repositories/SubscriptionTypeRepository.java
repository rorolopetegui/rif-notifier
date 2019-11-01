package org.rif.notifier.repositories;

import org.rif.notifier.models.entities.SubscriptionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionTypeRepository extends JpaRepository<SubscriptionType, String> {
    List<SubscriptionType> findBySubscriptionType(int subscriptionType);
}
