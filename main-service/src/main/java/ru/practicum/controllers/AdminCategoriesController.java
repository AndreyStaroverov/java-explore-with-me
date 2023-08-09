package ru.practicum.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.service.AdminCategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/admin/categories")
@Validated
public class AdminCategoriesController {

    private final AdminCategoryService adminCategoryService;

    public AdminCategoriesController(@Autowired AdminCategoryService adminCategoryService) {
        this.adminCategoryService = adminCategoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto postCat(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        return adminCategoryService.postCategory(newCategoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCat(@PathVariable @Valid @Positive Long catId) {
        adminCategoryService.deleteCat(catId);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto patchCat(@PathVariable @Valid @Positive Long catId,
                                @Valid @RequestBody NewCategoryDto newCategoryDto) {
        return adminCategoryService.patchCategory(catId, newCategoryDto);
    }

}
