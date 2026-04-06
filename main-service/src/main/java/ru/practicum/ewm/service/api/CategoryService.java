package ru.practicum.ewm.service.api;

import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createAdminCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateAdminCategory(Long catId, CategoryDto categoryDto);

    void deleteAdminCategory(Long catId);

    List<CategoryDto> getPublicCategories(Integer from, Integer size);

    CategoryDto getPublicCategoryById(Long catId);
}