package com.perfstor.perfstor.repository;

import com.perfstor.perfstor.model.Run;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RunRepository extends JpaRepository<Run, Long> {
}
