package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.exception.CategoryConflictException;
import ru.practicum.ewm.exception.CategoryNotFoundException;
import ru.practicum.ewm.exception.CategoryValidationException;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.service.api.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto createAdminCategory(NewCategoryDto newCategoryDto) {

        checkCategoryName(newCategoryDto.getName());
        checkDuplicateCategoryName(newCategoryDto.getName());
        Category category = CategoryMapper.mapNewCategoryDtoToCategory(newCategoryDto);
        categoryRepository.save(category);

        return CategoryMapper.mapToCategoryDto(category);
    }

    @Override
    public CategoryDto updateAdminCategory(Long catId, CategoryDto categoryDto) {

        Category oldCategory = categoryRepository.findById(catId).orElseThrow(() ->
                new CategoryNotFoundException("Category с таким id = " + catId + " не найдена"));

        if (!categoryDto.getName().equals(oldCategory.getName())) {
            checkCategoryName(categoryDto.getName());
            checkDuplicateCategoryName(categoryDto.getName());
        }
        Category newCategory = new Category(oldCategory.getId(), categoryDto.getName());
        categoryRepository.save(newCategory);

        return CategoryMapper.mapToCategoryDto(newCategory);
    }

    @Override
    public void deleteAdminCategory(Long catId) {

        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new CategoryNotFoundException("Category с таким id = " + catId + "не найдена"));
        checkCategoryEvent(catId);

        categoryRepository.delete(category);
    }

    @Override
    public List<CategoryDto> getPublicCategories(Integer from, Integer size) {

        int page = from / size;
        return categoryRepository.findAllBy(PageRequest.of(page, size))
                .stream()
                .map(CategoryMapper::mapToCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getPublicCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new CategoryNotFoundException("Category с таким id = " + catId + "не найдена"));
        return CategoryMapper.mapToCategoryDto(category);
    }

    private void checkCategoryName(String categoryName) {
        if (categoryName == null || categoryName.isBlank() || categoryName.length() > 50) {
            throw new CategoryValidationException("Некорректное имя Category");
        }
    }

    private void checkDuplicateCategoryName(String categoryName) {
        if (categoryRepository.existsByName(categoryName)) {
            throw new CategoryConflictException("Category с таким именем уже существует");
        }
    }

    private void checkCategoryEvent(Long catId) {

        if (eventRepository.existsByCategoryId(catId)) {
            throw new CategoryConflictException("К данной Category " + catId + " привязано событие");
        }
    }
}