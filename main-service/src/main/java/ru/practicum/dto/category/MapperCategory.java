package ru.practicum.dto.category;

import ru.practicum.model.Category;

import java.util.ArrayList;
import java.util.Collection;

public class MapperCategory {

    public static Category toCategory(NewCategoryDto newCategoryDto) {
        return Category.builder()
                .id(0L)
                .name(newCategoryDto.getName())
                .build();
    }

    public static CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Collection<CategoryDto> toCategoryDtoColl(Collection<Category> categorys) {
        Collection<CategoryDto> categoryDtos = new ArrayList<>();
        for (Category category : categorys) {
            categoryDtos.add(CategoryDto.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .build());
        }
        return categoryDtos;
    }
}
