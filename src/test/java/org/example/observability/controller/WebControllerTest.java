package org.example.observability.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.client.RestTestClient;

@ExtendWith(MockitoExtension.class)
class WebControllerTest {

    private RestTestClient restTestClient;

    @BeforeEach
    public void setup() {
        restTestClient = RestTestClient.bindToServer().build();
    }

    //    @Test
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
    }

}