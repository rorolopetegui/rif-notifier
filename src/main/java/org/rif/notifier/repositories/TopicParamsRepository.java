package org.rif.notifier.repositories;

import org.rif.notifier.models.entities.TopicParams;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicParamsRepository extends JpaRepository<TopicParams, String> {
    List<TopicParams> findByIdTopic(int id_topic);
}
