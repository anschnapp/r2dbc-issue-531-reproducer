package com.example.r2dbcissue531reproducer;

import com.example.r2dbcissue531reproducer.entity.TestEntity;
import com.example.r2dbcissue531reproducer.repository.TestEntityRepository;
import com.example.r2dbcissue531reproducer.service.TestEntityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;

@SpringBootTest
@Testcontainers
class R2dbcIssue531ReproducerApplicationTests {

    @Autowired
    TestEntityService service;
    @Autowired
    TestEntityRepository repository;
    @Autowired
    DatabaseClient databaseClient;

    @Container
    private static final PostgreSQLContainer<?> postgresSQLContainer = new PostgreSQLContainer<>()
            .withUsername("user")
            .withPassword("pass")
            .withDatabaseName("database_name")
            .waitingFor(new WaitAllStrategy()
                    .withStrategy(new LogMessageWaitStrategy().withRegEx(".*database system is ready to accept connections.*\\s"))
                    .withStrategy(new HostPortWaitStrategy()));

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> "r2dbc:postgresql://127.0.0.1:" + postgresSQLContainer.getMappedPort(5432) + "/database_name");
    }


    @BeforeEach
    void prepareInitData() {
        databaseClient.sql("CREATE TABLE IF NOT EXISTS test_entity (id SERIAL PRIMARY KEY , name varchar(255))").then()
                .then(repository.deleteAll()
                        .then(repository.save(new TestEntity("first test entity")))
                ).block();
    }

    @Test
    void reproduceStuckInTx() {
        Flux.range(0, 1000)
                .parallel()
                .flatMap(i -> service.doSelectInTx())
                .sequential()
                .collectList().block();
    }
}
