import org.example.observability.SpringObservabilityApplication;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

//@DataJpaTest
@SpringBootTest(classes = SpringObservabilityApplication.class)
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@AutoConfigureTestDatabase(replace = NONE)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"             // Forces Hibernate to build the schema for tests
})
public class DBTestContainerIT {

    private static final Logger log = LoggerFactory.getLogger(DBTestContainerIT.class);


    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:18");

    @BeforeAll
    public static void setup() {
    }


    @Test
    public void shouldTestPostgresContainer() {
        assertTrue(postgreSQLContainer.isCreated(), "");
        String testQueryString = postgreSQLContainer.getTestQueryString();
        log.info("testQueryString : {}", testQueryString);
        log.info("Exposed ports: {}", postgreSQLContainer.getExposedPorts());
    }


//    @Container
//    @ServiceConnection
//    static KafkaContainer kafkaContainer = new KafkaContainer("apache/kafka");


//    @Test
//    public void shouldTestKafkaContainer() {
//        assertTrue(kafkaContainer.isCreated(), "");
//        log.info("getContainerName : {}", kafkaContainer.getContainerName());
//        log.info("Exposed ports: {}", kafkaContainer.getExposedPorts());
//    }


}
