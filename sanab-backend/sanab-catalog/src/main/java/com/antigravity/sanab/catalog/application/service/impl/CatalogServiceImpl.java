package com.antigravity.sanab.catalog.application.service.impl;

import com.antigravity.sanab.catalog.api.dto.request.CreateProductRequest;
import com.antigravity.sanab.catalog.api.dto.response.BrandResponse;
import com.antigravity.sanab.catalog.api.dto.response.CategoryResponse;
import com.antigravity.sanab.catalog.api.dto.response.ProductResponse;
import com.antigravity.sanab.catalog.application.service.CatalogService;
import com.antigravity.sanab.catalog.domain.entity.*;
import com.antigravity.sanab.catalog.domain.enums.ProductStatus;
import com.antigravity.sanab.catalog.domain.enums.ProductType;
import com.antigravity.sanab.catalog.domain.repository.BrandRepository;
import com.antigravity.sanab.catalog.domain.repository.CategoryRepository;
import com.antigravity.sanab.catalog.domain.repository.ProductRepository;
import com.antigravity.sanab.shared.api.response.PagedResponse;
import com.antigravity.sanab.shared.exception.ErrorCode;
import com.antigravity.sanab.shared.exception.SanabException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CatalogServiceImpl implements CatalogService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    @Override
    public ProductResponse createProduct(CreateProductRequest req) {
        if (productRepository.existsBySku(req.sku())) {
            throw new SanabException(ErrorCode.PRODUCT_ALREADY_EXISTS, "SKU already exists: " + req.sku());
        }

        String slug = toSlug(req.title());
        if (productRepository.existsBySlug(slug)) {
            slug = slug + "-" + UUID.randomUUID().toString().substring(0, 6);
        }

        Category category = categoryRepository.findById(req.categoryId())
                .orElseThrow(() -> new SanabException(ErrorCode.CATEGORY_NOT_FOUND, "Category not found"));

        Brand brand = null;
        if (req.brandId() != null) {
            brand = brandRepository.findById(req.brandId())
                    .orElseThrow(() -> new SanabException(ErrorCode.BRAND_NOT_FOUND, "Brand not found"));
        }

        Product product = Product.builder()
                .title(req.title().strip())
                .slug(slug)
                .sku(req.sku().strip())
                .description(req.description())
                .shortDescription(req.shortDescription())
                .basePrice(req.basePrice())
                .salePrice(req.salePrice())
                .productType(req.productType())
                .status(ProductStatus.ACTIVE)
                .category(category)
                .brand(brand)
                .purity(req.purity())
                .metalColor(req.metalColor())
                .metalWeightGrams(req.metalWeightGrams())
                .gemstoneDetails(req.gemstoneDetails())
                .hallmarkingCertification(req.hallmarkingCertification())
                .shadeName(req.shadeName())
                .volumeMl(req.volumeMl())
                .skinType(req.skinType())
                .ingredientList(req.ingredientList())
                .featured(req.featured())
                .bestseller(req.bestseller())
                .totalStockQuantity(req.initialStockQuantity())
                .build();

        if (req.imageUrls() != null && !req.imageUrls().isEmpty()) {
            List<ProductImage> images = new ArrayList<>();
            for (int i = 0; i < req.imageUrls().size(); i++) {
                images.add(ProductImage.builder()
                        .product(product)
                        .url(req.imageUrls().get(i))
                        .displayOrder(i)
                        .primary(i == 0)
                        .build());
            }
            product.setImages(images);
        }

        Product saved = productRepository.save(product);
        log.info("Created product: id={}, sku={}", saved.getId(), saved.getSku());
        return mapToProductResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new SanabException(ErrorCode.PRODUCT_NOT_FOUND, "Product not found: " + slug));
        return mapToProductResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new SanabException(ErrorCode.PRODUCT_NOT_FOUND, "Product not found"));
        return mapToProductResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> getAllProducts(Pageable pageable) {
        Page<ProductResponse> page = productRepository.findByStatus(ProductStatus.ACTIVE, pageable)
                .map(this::mapToProductResponse);
        return PagedResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> getProductsByType(ProductType type, Pageable pageable) {
        Page<ProductResponse> page = productRepository.findByProductTypeAndStatus(type, ProductStatus.ACTIVE, pageable)
                .map(this::mapToProductResponse);
        return PagedResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> getProductsByCategory(UUID categoryId, Pageable pageable) {
        Page<ProductResponse> page = productRepository.findByCategoryIdAndStatus(categoryId, ProductStatus.ACTIVE, pageable)
                .map(this::mapToProductResponse);
        return PagedResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> getProductsByBrand(UUID brandId, Pageable pageable) {
        Page<ProductResponse> page = productRepository.findByBrandIdAndStatus(brandId, ProductStatus.ACTIVE, pageable)
                .map(this::mapToProductResponse);
        return PagedResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> getFeaturedProducts(Pageable pageable) {
        Page<ProductResponse> page = productRepository.findByFeaturedTrueAndStatus(ProductStatus.ACTIVE, pageable)
                .map(this::mapToProductResponse);
        return PagedResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> getBestsellerProducts(Pageable pageable) {
        Page<ProductResponse> page = productRepository.findByBestsellerTrueAndStatus(ProductStatus.ACTIVE, pageable)
                .map(this::mapToProductResponse);
        return PagedResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoryTree() {
        List<Category> roots = categoryRepository.findByParentIsNullAndActiveTrueOrderByDisplayOrderAsc();
        return roots.stream().map(this::mapToCategoryResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandResponse> getAllActiveBrands() {
        return brandRepository.findByActiveTrueOrderByNameAsc().stream()
                .map(b -> new BrandResponse(b.getId(), b.getName(), b.getSlug(), b.getLogoUrl(), b.getDescription(), b.getWebsiteUrl(), b.isActive(), b.isFeatured()))
                .toList();
    }

    private ProductResponse mapToProductResponse(Product p) {
        List<ProductResponse.ProductImageResponse> imageResponses = p.getImages() == null ? List.of() :
                p.getImages().stream().map(img -> new ProductResponse.ProductImageResponse(img.getId(), img.getUrl(), img.getAltText(), img.getDisplayOrder(), img.isPrimary())).toList();

        List<ProductResponse.ProductVariantResponse> variantResponses = p.getVariants() == null ? List.of() :
                p.getVariants().stream().map(v -> new ProductResponse.ProductVariantResponse(v.getId(), v.getSku(), v.getVariantName(), v.getPriceOverride(), v.getStockQuantity(), v.getAttributeName(), v.getAttributeValue())).toList();

        return new ProductResponse(
                p.getId(),
                p.getTitle(),
                p.getSlug(),
                p.getSku(),
                p.getDescription(),
                p.getShortDescription(),
                p.getBasePrice(),
                p.getSalePrice(),
                p.getProductType(),
                p.getStatus(),
                p.getCategory().getId(),
                p.getCategory().getName(),
                p.getBrand() == null ? null : p.getBrand().getId(),
                p.getBrand() == null ? null : p.getBrand().getName(),
                p.getPurity(),
                p.getMetalColor(),
                p.getMetalWeightGrams(),
                p.getGemstoneDetails(),
                p.getHallmarkingCertification(),
                p.getShadeName(),
                p.getVolumeMl(),
                p.getSkinType(),
                p.getIngredientList(),
                p.isFeatured(),
                p.isBestseller(),
                p.getTotalStockQuantity(),
                imageResponses,
                variantResponses
        );
    }

    private CategoryResponse mapToCategoryResponse(Category c) {
        List<CategoryResponse> sub = c.getSubcategories() == null ? List.of() :
                c.getSubcategories().stream().map(this::mapToCategoryResponse).toList();
        return new CategoryResponse(
                c.getId(), c.getName(), c.getSlug(), c.getDescription(), c.getImageUrl(),
                c.getParent() == null ? null : c.getParent().getId(),
                c.getDisplayOrder(), c.isActive(), c.isFeatured(), sub
        );
    }

    private static String toSlug(String input) {
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }
}
