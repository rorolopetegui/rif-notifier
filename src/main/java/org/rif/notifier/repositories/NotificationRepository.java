package org.rif.notifier.repositories;

import org.rif.notifier.models.entities.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findAllByToAddressAndIdGreaterThanAndIdTopicIn(String to_address, Integer id, Set<Integer> idTopic, Pageable pageable);

    @Query(value = "SELECT * FROM notification A WHERE A.to_address = ?1 AND A.id > ?2", nativeQuery = true)
    List<Notification> findAllByToAddressAndIdGraterThan(String to_address, Integer id, Pageable pageable);

    List<Notification> findAllByToAddressAndIdTopicIn(String to_address, Set<Integer> idTopic, Pageable pageable);

    List<Notification> findAllByToAddress(String to_address, Pageable pageable);
}
