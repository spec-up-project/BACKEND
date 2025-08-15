package com.neekly_report.whirlwind.mapper;

import com.neekly_report.whirlwind.dto.CategoryDto;
import com.neekly_report.whirlwind.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    // CreateRequest → Entity
    Category toEntity(CategoryDto.Request.CategoryCreateRequest dto);

    // UpdateRequest → Entity
    Category toEntity(CategoryDto.Request.CategoryUpdateRequest dto);

    // Entity → Response DTO
    CategoryDto.Response.CategoryResponse toResponse(Category entity);
}

