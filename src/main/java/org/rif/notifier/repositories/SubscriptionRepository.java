package org.rif.notifier.repositories;

import org.rif.notifier.models.entities.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, String> {

    Subscription findByUserAddress(String user_address);

    List<Subscription> findByActive(int active);

    @Query(value = "SELECT * FROM subscription A JOIN user_topic B ON A.user_address=B.user_address AND A.active = 1 AND B.id_topic = ?1", nativeQuery = true)
    List<Subscription> findByIdTopicAndSubscriptionActive(int id);
}
