package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
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
        Category category = CategoryMapper.mapNewCategoryDtoToCategory(newCategoryDto);
        categoryRepository.save(category);
        return CategoryMapper.mapToCategoryDto(category);
    }

    @Override
    public CategoryDto updateAdminCategory(Long catId, CategoryDto categoryDto) {
        Category oldCategory = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category с таким id = " + catId + " не найдена"));

        oldCategory.setName(categoryDto.getName());
        categoryRepository.save(oldCategory);

        return CategoryMapper.mapToCategoryDto(oldCategory);
    }

    @Override
    public void deleteAdminCategory(Long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException("Category с таким id = " + catId + " не найдена");
        }

        if (eventRepository.existsByCategoryId(catId)) {
            throw new ConflictException("К данной Category " + catId + " привязано событие");
        }

        categoryRepository.deleteById(catId);
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
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category с таким id = " + catId + " не найдена"));
        return CategoryMapper.mapToCategoryDto(category);
    }
}