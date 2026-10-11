import com.redis.testcontainers.RedisContainer;
import org.example.observability.SpringObservabilityApplication;
import org.example.observability.repository.PGDBRepository;
import org.example.observability.repository.WeatherEntity;
import org.example.observability.service.WeatherService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

//@DataJpaTest
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@AutoConfigureTestDatabase(replace = NONE)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"             // Forces Hibernate to build the schema for tests since Testcontainers are ephemeral with no pre-existing schema to validate against.
})
@SpringBootTest(classes = SpringObservabilityApplication.class)
public class DBTestContainerIT {

    private static final Logger log = LoggerFactory.getLogger(DBTestContainerIT.class);


    @MockitoSpyBean
    @Autowired
    private PGDBRepository pgdbRepository;

    @Autowired
    private WeatherService weatherService;


    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:18");

    @Container
    @ServiceConnection
    static RedisContainer redisContainer = new RedisContainer("redis:8");

    @BeforeAll
    public static void setup() {
    }


    @Test
    public void shouldTestPostgresContainer() {
        assertTrue(postgreSQLContainer.isCreated(), "");
        String testQueryString = postgreSQLContainer.getTestQueryString();
        log.info("testQueryString : {}", testQueryString);
        log.info("Exposed ports: {}", postgreSQLContainer.getExposedPorts());

        WeatherEntity cincinnati = weatherService.getWeatherForCity("Cincinnati");

        assertFalse(cincinnati.getTemperature().isBlank());
        verify(pgdbRepository, times(1)).save(any());
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
