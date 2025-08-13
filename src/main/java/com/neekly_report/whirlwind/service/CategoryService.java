package com.neekly_report.whirlwind.service;

import com.neekly_report.whirlwind.dto.CategoryDto;
import com.neekly_report.whirlwind.entity.Category;
import com.neekly_report.whirlwind.entity.User;
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

    public CategoryDto.Response.CategoryResponse createCategory(String tUserUid, CategoryDto.Request.CategoryCreateRequest dto) {
        User user = userRepository.findById(tUserUid)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        Category category = Category.builder()
                .categoryName(dto.getCategoryName())
                .segType(dto.getSegType())
                .user(user)
                .build();

        Category saved = categoryRepository.save(category);

        return CategoryDto.Response.CategoryResponse.builder()
                .tCategoryUid(saved.getTCategoryUid())
                .categoryName(saved.getCategoryName())
                .segType(saved.getSegType())
                .createDate(saved.getCreateDate())
                .modifyDate(saved.getModifyDate())
                .build();
    }

    public List<CategoryDto.Response.CategoryResponse> getUserCategories(String tUserUid) {
        return categoryRepository.findByUser_tUserUid(tUserUid)
                .stream()
                .map(c -> CategoryDto.Response.CategoryResponse.builder()
                        .tCategoryUid(c.getTCategoryUid())
                        .categoryName(c.getCategoryName())
                        .segType(c.getSegType())
                        .createDate(c.getCreateDate())
                        .modifyDate(c.getModifyDate())
                        .build())
                .collect(Collectors.toList());
    }

    public List<CategoryDto.Response.CategoryResponse> getCategoriesBySegType(String tUserUid, String segType) {
        return categoryRepository.findByUser_tUserUidAndSegType(tUserUid, segType)
                .stream()
                .map(c -> CategoryDto.Response.CategoryResponse.builder()
                        .tCategoryUid(c.getTCategoryUid())
                        .categoryName(c.getCategoryName())
                        .segType(c.getSegType())
                        .createDate(c.getCreateDate())
                        .modifyDate(c.getModifyDate())
                        .build())
                .collect(Collectors.toList());
    }

    public CategoryDto.Response.CategoryResponse updateCategory(String tUserUid, CategoryDto.Request.CategoryUpdateRequest dto) {
        Category category = categoryRepository.findByTCategoryUidAndUser_tUserUid(tUserUid, dto.getTCategoryUid());
        
        if (category == null) {
            throw new RuntimeException("카테고리를 찾을 수 없거나 접근 권한이 없습니다");
        }

        category.setCategoryName(dto.getCategoryName());
        category.setSegType(dto.getSegType());

        Category saved = categoryRepository.save(category);

        return CategoryDto.Response.CategoryResponse.builder()
                .tCategoryUid(saved.getTCategoryUid())
                .categoryName(saved.getCategoryName())
                .segType(saved.getSegType())
                .createDate(saved.getCreateDate())
                .modifyDate(saved.getModifyDate())
                .build();
    }

    public String deleteCategory(String tUserUid, String tCategoryUid) {
        try {
            Category category = categoryRepository.findByTCategoryUidAndUser_tUserUid(tUserUid, tCategoryUid);
            
            if (category == null) {
                throw new RuntimeException("카테고리를 찾을 수 없거나 접근 권한이 없습니다");
            }

            categoryRepository.delete(category);
            return tCategoryUid;
        } catch (Exception e) {
            return e.getStackTrace()[0].toString() + " : " + e.getMessage() + " : " + tCategoryUid;
        }
    }
}