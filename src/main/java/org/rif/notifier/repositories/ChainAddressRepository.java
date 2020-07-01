package org.rif.notifier.repositories;

import org.rif.notifier.models.entities.ChainAddressEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChainAddressRepository extends JpaRepository<ChainAddressEvent, String> {
}
