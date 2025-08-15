
package com.neekly_report.whirlwind.service;

import com.neekly_report.whirlwind.dto.CategoryDto;
import com.neekly_report.whirlwind.entity.Category;
import com.neekly_report.whirlwind.entity.User;
import com.neekly_report.whirlwind.mapper.CategoryMapper;
import com.neekly_report.whirlwind.repository.CategoryRepository;
import com.neekly_report.whirlwind.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryDto.Response.CategoryResponse> getUserCategories(String tUserUid) {
        return categoryRepository.findByUser_userUid(tUserUid)
                .stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<CategoryDto.Response.CategoryResponse> getCategoriesBySegType(String tUserUid, String segType) {
        return categoryRepository.findByUser_userUidAndSegType(tUserUid, segType)
                .stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    public CategoryDto.Response.CategoryResponse createCategory(String tUserUid, CategoryDto.Request.CategoryCreateRequest dto) {
        User user = userRepository.findById(tUserUid)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        Category category = categoryMapper.toEntity(dto);
        category.setUser(user);

        Category saved = categoryRepository.save(category);
        return categoryMapper.toResponse(saved);
    }

    public CategoryDto.Response.CategoryResponse updateCategory(String tUserUid, CategoryDto.Request.CategoryUpdateRequest dto) {
        Category category = categoryRepository.findByCategoryUidAndUser_userUid(tUserUid, dto.getCategoryUid());

        if (category == null) {
            throw new RuntimeException("카테고리를 찾을 수 없거나 접근 권한이 없습니다");
        }

        category.setCategoryName(dto.getCategoryName());
        category.setSegType(dto.getSegType());

        Category saved = categoryRepository.save(category);
        return categoryMapper.toResponse(saved);
    }

    public String deleteCategory(String tUserUid, String categoryUid) {
        try {
            Category category = categoryRepository.findByCategoryUidAndUser_userUid(tUserUid, categoryUid);

            if (category == null) {
                throw new RuntimeException("카테고리를 찾을 수 없거나 접근 권한이 없습니다");
            }

            categoryRepository.delete(category);
            return categoryUid;
        } catch (Exception e) {
            return e.getStackTrace()[0].toString() + " : " + e.getMessage() + " : " + categoryUid;
        }
    }
}
