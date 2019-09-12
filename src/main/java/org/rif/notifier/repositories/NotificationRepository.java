package org.rif.notifier.repositories;

import org.rif.notifier.models.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface NotificationRepository extends JpaRepository<Notification, String> {
}
