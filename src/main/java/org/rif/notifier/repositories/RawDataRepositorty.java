package org.rif.notifier.repositories;


import org.rif.notifier.models.entities.RawData;
import org.rif.notifier.models.entities.Test;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawDataRepositorty extends JpaRepository<RawData, String> {

}

