package com.antigravity.sanab.search.application.service;

import com.antigravity.sanab.search.api.dto.request.SearchRequest;
import com.antigravity.sanab.search.api.dto.response.SearchResultResponse;

import java.util.List;

public interface SearchService {

    SearchResultResponse searchProducts(SearchRequest request);

    List<String> getAutocompleteSuggestions(String query);
}
