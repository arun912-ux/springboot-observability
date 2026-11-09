import com.redis.testcontainers.RedisContainer;
import org.example.observability.SpringObservabilityApplication;
import org.example.observability.repository.MongoDBRepository;
import org.example.observability.repository.PGDBRepository;
import org.example.observability.dto.WeatherEntity;
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
import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

//@DataJpaTest
//@ActiveProfiles("integration")
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
//    @Autowired
    private PGDBRepository pgdbRepository;

    @MockitoSpyBean
//    @Autowired
    private MongoDBRepository mongoDBRepository;

    @Autowired
    private WeatherService weatherService;


    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:18");

    @Container
    @ServiceConnection
    static RedisContainer redisContainer = new RedisContainer("redis:8");

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8.0");

    @BeforeAll
    public static void setup() {
    }


    @Test
    public void shouldTestPostgresContainer() throws ExecutionException, InterruptedException {
        assertTrue(postgreSQLContainer.isCreated(), "");
        String testQueryString = postgreSQLContainer.getTestQueryString();
        log.info("testQueryString : {}", testQueryString);
        log.info("Exposed ports: {}", postgreSQLContainer.getExposedPorts());

        WeatherEntity cincinnati = weatherService.getWeatherForCity("Cincinnati");

        assertFalse(cincinnati.getTemperature().isBlank());
        verify(pgdbRepository, times(1)).save(any());
        verify(mongoDBRepository, times(1)).save(any());
    }


    @Test
    public void someCollectionsTest() {
        List<Integer> list = Arrays.asList(1, 3, 0, 4, 2, 5, 8, 6, 11, 7, 13, 9, 10, 12);
        log.info("95 list : {}", list);
        Collections.reverse(list);
        log.info("97 reverse list : {}", list);
        Collections.sort(list, Integer::compare);
        log.info("99 sorted list : {}", list);
        Collections.sort(list, Collections.reverseOrder());
        log.info("101 reverse sorted list : {}", list);
        Collections.sort(list, Comparator.naturalOrder());
        log.info("104 natural order sorted list : {}", list);
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
