package com.antigravity.sanab.shared.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Paginated API response wrapper embedding pagination metadata.
 *
 * <p>Used for all list endpoints that return paginated results.
 * The outer {@link ApiResponse} wraps this as its {@code data} field.
 *
 * <p>Example:
 * <pre>{@code
 * {
 *   "content": [...],
 *   "page": 0,
 *   "size": 20,
 *   "totalElements": 150,
 *   "totalPages": 8,
 *   "first": true,
 *   "last": false
 * }
 * }</pre>
 *
 * @param <T>           element type of the content list
 * @param content       the page content
 * @param page          zero-based page index
 * @param size          number of elements per page
 * @param totalElements total number of elements across all pages
 * @param totalPages    total number of pages
 * @param first         whether this is the first page
 * @param last          whether this is the last page
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@JsonInclude(Include.NON_NULL)
public record PagedResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {

    /**
     * Constructs a {@link PagedResponse} from a Spring Data {@link Page}.
     *
     * @param springPage the Spring Data page result
     * @param <T>        element type
     * @return wrapped paged response
     */
    public static <T> PagedResponse<T> of(Page<T> springPage) {
        return new PagedResponse<>(
                springPage.getContent(),
                springPage.getNumber(),
                springPage.getSize(),
                springPage.getTotalElements(),
                springPage.getTotalPages(),
                springPage.isFirst(),
                springPage.isLast()
        );
    }
}
