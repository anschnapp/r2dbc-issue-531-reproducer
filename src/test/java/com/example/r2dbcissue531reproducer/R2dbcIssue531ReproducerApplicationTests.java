package com.example.r2dbcissue531reproducer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class R2dbcIssue531ReproducerApplicationTests {
	@Container
	private static final PostgreSQLContainer<?> postgresSQLContainer = new PostgreSQLContainer<>()
			.withUsername("user")
			.withPassword("pass")
			.waitingFor(new WaitAllStrategy()
					.withStrategy(new LogMessageWaitStrategy().withRegEx(".*database system is ready to accept connections.*\\s"))
					.withStrategy(new HostPortWaitStrategy()));


	@Test
	void contextLoads() {
	}

}
