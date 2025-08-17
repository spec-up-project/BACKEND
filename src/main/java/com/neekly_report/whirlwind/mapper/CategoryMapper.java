package com.neekly_report.whirlwind.mapper;

import com.neekly_report.whirlwind.dto.CategoryDto;
import com.neekly_report.whirlwind.entity.Category;
import org.mapstruct.Mapper;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    // CreateRequest → Entity
    Category toEntity(CategoryDto.Request.CategoryCreateRequest dto);

    // UpdateRequest → Entity
    Category toEntity(CategoryDto.Request.CategoryUpdateRequest dto);

    // Response → Entity
    Category toEntity(CategoryDto.Response.CategoryResponse dto);

    // Entity → Response DTO
    CategoryDto.Response.CategoryResponse toResponse(Category entity);

    // Helper to map a single Category entity to its Response DTO
    CategoryDto.Response.CategoryResponse toCategoryResponse(Category entity);

    // Provide a method for MapStruct to map Category -> List<CategoryResponse>
    default List<CategoryDto.Response.CategoryResponse> map(Category value) {
        if (value == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(toCategoryResponse(value));
    }
}

