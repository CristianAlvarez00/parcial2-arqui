package com.udea.parcialFinal;

import com.udea.parcialFinal.repository.MateriaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;

// Levanta PostgreSQL y Redis reales en Docker con las mismas imágenes de compose.yaml.
@SpringBootTest(properties = "metrics.password=test-secret")
@Testcontainers
class ParcialFinalApplicationTests {

	@Container
	@ServiceConnection
	static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");

	@Container
	@ServiceConnection(name = "redis")
	static GenericContainer<?> redis = new GenericContainer<>("redis:7.4-alpine").withExposedPorts(6379);

	@Autowired
	private MateriaRepository materias;

	@Test
	void contextLoadsYFlywayPrecargaLasMaterias() {
		assertThat(materias.count()).isEqualTo(5);
	}

}
