package org.example.observability.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTimeout;

@ExtendWith(MockitoExtension.class)
class WebControllerTest {

    private RestTestClient restTestClient;

    @Mock
    MockMvc mockMvc;
    @Spy
    MockMvc mockMvc2;

    @BeforeEach
    public void setup() {
        restTestClient = RestTestClient.bindTo(mockMvc).build();
    }

    @Test
    @Timeout(value = 2000, unit = TimeUnit.SECONDS)
    public void testGetWeather() {
        restTestClient.get()
                .uri(url -> url
                        .path("/weather")
                        .queryParam("city", "Hyderabad")
                        .build()
                )
                .exchange()
                .expectBody()
                .returnResult().getStatus().is2xxSuccessful();


        String s1 = "Hello";
        String s2 = new String("Hello");
        assertSame(1, Integer.valueOf(1));

        assertNotSame(s1, s2);
        assertEquals(s1, s2);

        assertTimeout(Duration.ofSeconds(2), () -> {
            Integer.parseInt("1");
            Thread.sleep(1000);
        });

//        doReturn(null).when(mockMvc).getDispatcherServlet();
//        doNothing().when(mockMvc2).getDispatcherServlet();

    }

}