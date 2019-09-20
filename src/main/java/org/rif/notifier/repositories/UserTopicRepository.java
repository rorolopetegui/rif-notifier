package org.rif.notifier.repositories;

import org.rif.notifier.models.entities.UserTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTopicRepository extends JpaRepository<UserTopic, String> {

}
