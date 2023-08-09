package ru.practicum.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.service.CategoriesService;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.Collection;

@RestController
@RequestMapping(path = "/categories")
@Validated
public class CategoriesController {

    private final CategoriesService categoriesService;

    public CategoriesController(@Autowired CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<CategoryDto> getCategories(@RequestParam(name = "from", required = false, defaultValue = "0")
                                                 @Min(0) Long from,
                                                 @RequestParam(name = "size", required = false, defaultValue = "10")
                                                 @Min(1) Long size) {
        return categoriesService.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategoriesById(@PathVariable @Positive Long catId) {
        return categoriesService.getCategoriesById(catId);
    }
}
