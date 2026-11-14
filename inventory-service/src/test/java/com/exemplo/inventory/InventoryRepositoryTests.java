package com.exemplo.inventory;

import com.exemplo.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class InventoryRepositoryTests {

    @Autowired
    InventoryRepository inventoryRepository;

    /* Deve retornar true quando o produto existe e
    a quantidade solicitada é menor ou igual ao estoque disponível */
    @Test
    void shouldBeInStockWhenQuantityIsAvailable() {
        assertThat(inventoryRepository
                .existsBySkuCodeAndQuantityGreaterThanEqual
                        ("iphone_15", 100))
                .isTrue();
    }

    /* Deve retornar false quando o produto existe,
    mas a quantidade solicitada excede o estoque disponível */
    @Test
    void shouldNotBeInStockWhenQuantityExceedsStock() {
        assertThat(inventoryRepository
                .existsBySkuCodeAndQuantityGreaterThanEqual
                        ("iphone_15", 101))
                .isFalse();
    }

    /* Deve retornar false quando o produto não existe no banco de dados */
    @Test
    void shouldNotBeInStockForUnknownSku() {
        assertThat(inventoryRepository
                .existsBySkuCodeAndQuantityGreaterThanEqual
                        ("nao_existe", 1))
                .isFalse();
    }

}
