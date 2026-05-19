package com.example.crud.jwtspringsecurity.controller;


import com.example.crud.jwtspringsecurity.dto.ApiResponse;
import com.example.crud.jwtspringsecurity.dto.ProductRequest;
import com.example.crud.jwtspringsecurity.dto.ProductResponse;
import com.example.crud.jwtspringsecurity.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Product REST Controller — demonstrates role-based authorization.
 *
 * Access summary:
 * ┌────────────────────────────────────────────┬───────────────────────┐
 * │ Endpoint                                   │ Required Role         │
 * ├────────────────────────────────────────────┼───────────────────────┤
 * │ GET  /api/products                         │ USER or ADMIN         │
 * │ GET  /api/products/{id}                    │ USER or ADMIN         │
 * │ GET  /api/products/category/{category}     │ USER or ADMIN         │
 * │ GET  /api/products/search?name=...         │ USER or ADMIN         │
 * │ POST /api/products                         │ ADMIN only            │
 * │ PUT  /api/products/{id}                    │ ADMIN only            │
 * │ DELETE /api/products/{id}                  │ ADMIN only            │
 * └────────────────────────────────────────────┴───────────────────────┘
 *
 * @PreAuthorize checks are evaluated at method entry.
 * If the user lacks the required role, Spring throws AccessDeniedException → 403.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ─── READ — accessible to USER and ADMIN ─────────────────────────────────

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success(
                "Fetched " + products.size() + " products", products));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Product found", productService.getProductById(id)));
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAvailableProducts() {
        List<ProductResponse> products = productService.getAvailableProducts();
        return ResponseEntity.ok(ApiResponse.success("Available products", products));
    }

    @GetMapping("/category/{category}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getByCategory(
            @PathVariable String category) {
        List<ProductResponse> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(ApiResponse.success(
                "Products in category: " + category, products));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> searchProducts(
            @RequestParam String name) {
        List<ProductResponse> products = productService.searchProducts(name);
        return ResponseEntity.ok(ApiResponse.success(
                "Search results for: " + name, products));
    }

    // ─── WRITE — ADMIN only ───────────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductRequest request) {
        ProductResponse created = productService.createProduct(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse updated = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully"));
    }
}
