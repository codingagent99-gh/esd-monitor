package com.esd.repository;

import com.esd.model.WmEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WmEventRepository extends JpaRepository<WmEvent, Long> {
}
