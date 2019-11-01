package org.rif.notifier.repositories;

import org.rif.notifier.models.entities.NotificationCounter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationCounterRepository extends JpaRepository<NotificationCounter, String> {
}
