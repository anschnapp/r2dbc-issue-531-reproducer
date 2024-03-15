package com.example.r2dbcissue531reproducer.repository;

import com.example.r2dbcissue531reproducer.entity.TestEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestEntityRepository extends R2dbcRepository<TestEntity, Long> {

}
