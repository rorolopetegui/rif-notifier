package org.rif.notifier.repositories;

import org.rif.notifier.models.entities.PreloadedEvents;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreloadedEventsRepository extends JpaRepository<PreloadedEvents, String> {
    PreloadedEvents findById(int id);
}
