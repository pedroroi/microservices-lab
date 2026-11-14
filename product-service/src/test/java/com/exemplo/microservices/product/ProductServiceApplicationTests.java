package com.exemplo.microservices.product;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceApplicationTests {

	@LocalServerPort
	int port;

	@BeforeEach
	void setup() {
		RestAssured.port = port;
	}

	@Test
	void shouldCreateProduct() {
		String requestBody = """
				{
					"name": "Iphone 15",
					"description": "Iphone 15 is a smartphone by Apple",
					"price": 1000
				}
				""";

		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.post("/api/product")
				.then()
				.statusCode(201)
				.body("id", Matchers.notNullValue())
				.body("name", Matchers.equalTo("Iphone 15"))
				.body("description", Matchers.equalTo("Iphone 15 is a smartphone by Apple"))
				.body("price", Matchers.equalTo(1000));
	}

	@Test
	void shouldGetAllProducts() {
		String requestBody = """
				{
					"name": "Iphone 15",
					"description": "Iphone 15 is a smartphone by Apple",
					"price": 1000
				}
				""";

		// Crio o produti
		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.post("/api/product")
				.then()
				.statusCode(201);

		// Verifico se o produto veio na listagem
		RestAssured.given()
				.when()
				.get("/api/product")
				.then()
				.statusCode(200)
				.body("name", Matchers.hasItem("Iphone 15"));
	}

}
