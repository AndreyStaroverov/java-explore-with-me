package ru.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.MapperCategory;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.handler.ConflictStateException;
import ru.practicum.handler.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;

import javax.validation.ConstraintViolationException;

@Service
public class AdminCategoryService {

    private final CategoryRepository categoryRepository;

    public AdminCategoryService(@Autowired CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CategoryDto postCategory(NewCategoryDto newCategoryDto) {
        try {
            return MapperCategory.toCategoryDto(categoryRepository.save(MapperCategory.toCategory(newCategoryDto)));
        } catch (Exception e) {
            throw new ConflictStateException(String.format("Category with name =%s already exist", newCategoryDto.getName()));
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteCat(Long catId) {
        checkCat(catId);
        try {
            categoryRepository.deleteById(catId);
        } catch (ConstraintViolationException e) {
            throw new ConflictStateException("The category is not empty");
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CategoryDto patchCategory(Long catId, NewCategoryDto newCategoryDto) {
        try {
            checkCat(catId);
            Category category = categoryRepository.getReferenceById(catId);
            category.setName(newCategoryDto.getName());
            return MapperCategory.toCategoryDto(categoryRepository.save(category));
        } catch (Exception e) {
            throw new ConflictStateException(String.format("Category with name =%s already exist", newCategoryDto.getName()));
        }
    }

    public void checkCat(Long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException(String.format("Category with id=%s was not found", catId));
        }
    }
}
