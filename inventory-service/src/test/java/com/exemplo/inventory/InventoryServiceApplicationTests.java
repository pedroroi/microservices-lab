package com.exemplo.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.client.RestTestClient;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventoryServiceApplicationTests {

	@LocalServerPort
	int port;

	RestTestClient client;

	@BeforeEach
	void setup() {
		client = RestTestClient.bindToServer()
				.baseUrl("http://localhost:" + port)
				.build();
	}

	/* Deve retornar 200 OK e true
	quando o produto possui estoque suficiente */
	@Test
	void shouldReturnTrueWhenItemIsInStock() {
		client.get()
				.uri(uriBuilder -> uriBuilder
						.path("/api/inventory")
						.queryParam("skuCode", "iphone_15")
						.queryParam("quantity", 100)
						.build())
				.exchange()
				.expectStatus().isOk()
				.expectBody(Boolean.class).isEqualTo(true);
	}

	/* Deve retornar 200 OK e false quando
	a quantidade solicitadaexcede o estoque */
	@Test
	void shouldReturnFalseWhenQuantityExceedsStock() {
		client.get()
				.uri(uriBuilder -> uriBuilder
						.path("/api/inventory")
						.queryParam("skuCode", "iphone_15")
						.queryParam("quantity", 101)
						.build())
				.exchange()
				.expectStatus().isOk()
				.expectBody(Boolean.class).isEqualTo(false);
	}

	/* Deve retornar 200 OK e false
	quando o SKU não existe */
	@Test
	void shouldReturnFalseWhenSkuDoesNotExist() {
		client.get()
				.uri(uriBuilder -> uriBuilder
						.path("/api/inventory")
						.queryParam("skuCode", "nao_existe")
						.queryParam("quantity", 1)
						.build())
				.exchange()
				.expectStatus().isOk()
				.expectBody(Boolean.class).isEqualTo(false);
	}

	/* Deve retornar 400 Bad Request
	quando parâmetros obrigatórios não são informados */
	@Test
	void shouldReturnBadRequestWhenQueryParamsAreMissing() {
		client.get()
				.uri("/api/inventory")
				.exchange()
				.expectStatus().isBadRequest();
	}

}
