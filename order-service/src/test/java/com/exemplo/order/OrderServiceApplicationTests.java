package com.exemplo.order;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderServiceApplicationTests {

	@LocalServerPort
	int port;

	@BeforeEach
	void setup() {
		RestAssured.port = port;
	}

	@Test
	void shouldSubmitOrder() {
		String submitOrderJson = """
				{
					"skuCode": "iphone_15",
					"price":	1000,
					"quantity": 1
				}
				""";

		RestAssured.given()
				.contentType("application/json")
				.body(submitOrderJson)
				.when()
				.post("/api/order")
				.then()
				.statusCode(201)
				.body(Matchers.equalTo("Pedido feito com sucesso!"));
	}

}
