package com.example.crud.jwtspringsecurity.service;

import com.example.crud.jwtspringsecurity.dto.ProductRequest;
import com.example.crud.jwtspringsecurity.dto.ProductResponse;
import com.example.crud.jwtspringsecurity.entity.Product;
import com.example.crud.jwtspringsecurity.exception.ResourceNotFoundException;
import com.example.crud.jwtspringsecurity.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Product Service — demonstrates role-protected CRUD operations.
 *
 * Access control is enforced at the controller layer via @PreAuthorize,
 * but the service stays clean and focused on business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    // ─── READ operations ──────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = findProductById(id);
        return mapToProductResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(String category) {
        return productRepository.findByCategory(category)
                .stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAvailableProducts() {
        return productRepository.findByIsAvailableTrue()
                .stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    // ─── WRITE operations (Admin only) ────────────────────────────────────────

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .category(request.getCategory())
                .isAvailable(request.isAvailable())
                .build();

        Product saved = productRepository.save(product);
        log.info("Product created: id={}, name={}", saved.getId(), saved.getName());
        return mapToProductResponse(saved);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = findProductById(id);

        // Update all fields (full replacement)
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(request.getCategory());
        product.setAvailable(request.isAvailable());

        Product updated = productRepository.save(product);
        log.info("Product updated: id={}", id);
        return mapToProductResponse(updated);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = findProductById(id);
        productRepository.delete(product);
        log.info("Product deleted: id={}", id);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .category(product.getCategory())
                .isAvailable(product.isAvailable())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
