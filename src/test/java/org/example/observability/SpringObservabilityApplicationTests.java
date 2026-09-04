package org.example.observability;

import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class SpringObservabilityApplicationTests {

    @Test
    void contextLoads() {
    }

    @BeforeAll
    public static void beforeClassTest() {

    }

    @BeforeEach
    public void beforeEach() {
        assertTrue(true);
        assertThrows(EntityExistsException.class, () -> {
            throw new EntityExistsException();
        });


    }


}
