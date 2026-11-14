package com.exemplo.inventory.repository;

import com.exemplo.inventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    // Consulta para saber se o produto do codigo x tem na quantidade igual ou superior
    boolean existsBySkuCodeAndQuantityGreaterThanEqual(String skuCode, Integer quantity);
}
