package org.rif.notifier.repositories;

import org.rif.notifier.models.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NotificationRepository extends JpaRepository<Notification, String> {
    public List<Notification> findByToAddress(String to_address);

    public List<Notification> findByToAddressAndIdTopic(String to_address, Integer idTopic);

    @Query(value = "SELECT * FROM notification A WHERE A.to_address = ?1 AND A.id > ?2", nativeQuery = true)
    public List<Notification> findByToAddressAndIdGraterThanId(String to_address, Integer id);

    @Query(value = "SELECT * FROM notification A WHERE A.to_address = ?1 AND A.id > ?2 AND A.id_topic = ?3", nativeQuery = true)
    public List<Notification> findByToAddressAndIdGraterThanIdAndIdTopic(String to_address, Integer id, Integer idTopic);

    @Query(value = "SELECT * FROM notification A WHERE A.to_address = ?1 ORDER BY A.id DESC LIMIT ?2", nativeQuery = true)
    public List<Notification> findByToAddressAndGetLastRows(String to_address, Integer lastRow);

    @Query(value = "SELECT * FROM notification A WHERE A.to_address = ?1 AND A.id_topic = ?2 ORDER BY A.id DESC LIMIT ?3", nativeQuery = true)
    public List<Notification> findByToAddressAndGetLastRowsAndIdTopic(String to_address, Integer idTopic, Integer lastRow);
}
