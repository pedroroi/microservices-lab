package com.exemplo.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderServiceApplicationTests {

	@LocalServerPort
	int port;

	RestTestClient client;

	@BeforeEach
	void setup() {
		client = RestTestClient.bindToServer()
				.baseUrl("http://localhost:" + port)
				.build();
	}

	@Test
	void shouldPlaceOrder() {
		client.post().uri("/api/order")
				.contentType(MediaType.APPLICATION_JSON)
				.body("""
						{"skuCode": "iphone_15", "price": 1000, "quantity":1}
						""")
				.exchange()
				.expectStatus().isCreated()
				.expectBody()
				.jsonPath("$.orderNumber").isNotEmpty()
				.jsonPath("$.skuCode").isEqualTo("iphone_15")
				.jsonPath("$.quantity").isEqualTo(1);
	}

	@Test
	void shouldRejectOrderWithoutQuantity() {
		client.post().uri("/api/order")
				.contentType(MediaType.APPLICATION_JSON)
				.body("""
						{"skuCode": "iphone_15", "price": 1000}
						""")
				.exchange()
				.expectStatus().isBadRequest();
	}

}
