package ru.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.MapperCategory;
import ru.practicum.handler.NotFoundException;
import ru.practicum.repository.CategoryRepository;

import java.util.Collection;

@Service
public class CategoriesService {

    private final CategoryRepository categoryRepository;

    public CategoriesService(@Autowired CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Collection<CategoryDto> getCategories(Long from, Long size) {
        Pageable page = PageRequest.of(Math.toIntExact(from / size), Math.toIntExact(size));
        return MapperCategory.toCategoryDtoColl(categoryRepository.findAll(page).toList());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public CategoryDto getCategoriesById(Long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException(String.format("Category with id=%s was not found", catId));
        }
        return MapperCategory.toCategoryDto(categoryRepository.getReferenceById(catId));
    }
}
