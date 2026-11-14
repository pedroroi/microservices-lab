package com.exemplo.microservices.product.service;

import com.exemplo.microservices.product.dto.ProductRequest;
import com.exemplo.microservices.product.dto.ProductResponse;
import com.exemplo.microservices.product.model.Product;
import com.exemplo.microservices.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor // não precisa escrever o construtor
@Slf4j //anotação para logs do lombok
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.name())
                .description(productRequest.description())
                .price(productRequest.price())
                .build();
        Product saved = productRepository.save(product);
        log.info("Produto criado: id={}", saved.getId());

        return new ProductResponse(
                saved.getId(),
                saved.getName(),
                saved.getDescription(),
                saved.getPrice());
    }


    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice()
                )).toList();
    }
}
