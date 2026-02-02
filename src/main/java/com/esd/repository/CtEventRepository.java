package com.esd.repository;

import com.esd.model.CtEvent;

import org.springframework.data.jpa.repository.JpaRepository;


public interface CtEventRepository extends  JpaRepository<CtEvent, Long> {
}
