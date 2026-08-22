package ru.practicum.ewm.server.category.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.server.category.dto.CategoryDto;
import ru.practicum.ewm.server.category.dto.NewCategoryDto;
import ru.practicum.ewm.server.category.service.CategoryService;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@Valid @RequestBody NewCategoryDto dto) {
        return categoryService.create(dto);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable long catId, @Valid @RequestBody CategoryDto dto) {
        return categoryService.update(catId, dto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable long catId) {
        categoryService.delete(catId);
    }
}
