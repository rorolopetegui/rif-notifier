package org.rif.notifier.repositories;

import org.rif.notifier.models.entities.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<Topic, String> {
    Topic findById(int id);
}
