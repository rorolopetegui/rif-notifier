package org.rif.notifier.repositories;

import org.rif.notifier.models.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findByToAddress(String to_address);

    List<Notification> findByToAddressAndIdTopicIn(String to_address, Set<Integer> idTopic);

    @Query(value = "SELECT * FROM notification A WHERE A.to_address = ?1 AND A.id > ?2", nativeQuery = true)
    List<Notification> findByToAddressAndIdGraterThanId(String to_address, Integer id);

    //findByToAddressAndIdGraterThanIdAndIdTopicIn
    //List<Notification> findBy(String to_address, Integer id, Set<Integer> idTopic);
    List<Notification> findAllByToAddressAndIdGreaterThanAndIdTopicIn(String to_address, Integer id, Set<Integer> idTopic);


    @Query(value = "SELECT * FROM notification A WHERE A.to_address = ?1 ORDER BY A.id DESC LIMIT ?2", nativeQuery = true)
    List<Notification> findByToAddressAndGetLastRows(String to_address, Integer lastRow);

    @Query(value = "SELECT * FROM notification A WHERE A.to_address = ?1 AND A.id_topic in (?2) ORDER BY A.id DESC LIMIT ?3", nativeQuery = true)
    List<Notification> findByToAddressAndGetLastRowsAndIdTopic(String to_address, Set<Integer> idTopics, Integer lastRow);
}
