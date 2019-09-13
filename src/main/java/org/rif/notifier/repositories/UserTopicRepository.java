package org.rif.notifier.repositories;

import org.rif.notifier.models.entities.UserTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTopicRepository extends JpaRepository<UserTopic, String> {
    public List<UserTopic> findByUserAddress(String user_address);

}
