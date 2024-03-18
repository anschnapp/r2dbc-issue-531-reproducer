package com.example.r2dbcissue531reproducer.service;

import com.example.r2dbcissue531reproducer.entity.TestEntity;
import com.example.r2dbcissue531reproducer.repository.TestEntityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Service
public class TestEntityService {
    private final TransactionalOperator transactionalOperator;
    private final TestEntityRepository testEntityRepository;

    public TestEntityService(TransactionalOperator transactionalOperator, TestEntityRepository testEntityRepository) {
        this.transactionalOperator = transactionalOperator;
        this.testEntityRepository = testEntityRepository;
    }

    public Mono<TestEntity> doProblematicSequence(int round) {
        return transactionalOperator
                .transactional(doProblematicSequenceWithouttx(round));
    }

    private Mono<TestEntity> doProblematicSequenceWithouttx(int round) {
        return Mono.fromFuture(writeAndReadOperation(round).toFuture());
    }

    private Mono<TestEntity> writeAndReadOperation(int round) {
        return testEntityRepository.save(new TestEntity("name entity round: " + round))
                .flatMap((entity) -> testEntityRepository.findByName("name entity round: " + round));
    }


}
