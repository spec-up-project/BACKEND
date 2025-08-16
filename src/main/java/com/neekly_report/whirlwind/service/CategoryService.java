
package com.neekly_report.whirlwind.service;

import com.neekly_report.whirlwind.dto.CategoryDto;
import com.neekly_report.whirlwind.entity.Category;
import com.neekly_report.whirlwind.entity.User;
import com.neekly_report.whirlwind.mapper.CategoryMapper;
import com.neekly_report.whirlwind.repository.CategoryRepository;
import com.neekly_report.whirlwind.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryDto.Response.CategoryResponse> getUserCategories(String tUserUid) {
        List<Category> categoryList = categoryRepository.findByUser_userUidOrderByCreateDateAsc(tUserUid);
        List<CategoryDto.Response.CategoryResponse> categoryResponses = categoryList.stream().map(categoryMapper::toResponse).toList();

        return buildCategoryTree(categoryResponses);
    }

    private List<CategoryDto.Response.CategoryResponse> buildCategoryTree(List<CategoryDto.Response.CategoryResponse> flatList) {
        Map<String, CategoryDto.Response.CategoryResponse> map = new HashMap<>();
        List<CategoryDto.Response.CategoryResponse> rootList = new ArrayList<>();


        // 1. 모든 카테고리를 맵에 저장
        for (CategoryDto.Response.CategoryResponse category : flatList) {
            // children이 null이면 빈 리스트로 초기화
            if (category.getChildren() == null) {
                category.setChildren(new ArrayList<>());
            }
            map.put(category.getCategoryUid(), category);
        }

        // 2. 부모-자식 관계 설정
        for (CategoryDto.Response.CategoryResponse category : flatList) {
            String parentUid = category.getParentUid();

            if (parentUid == null || parentUid.isEmpty()) {
                // 루트 노드
                rootList.add(category);
            } else {
                CategoryDto.Response.CategoryResponse parent = map.get(parentUid);
                if (parent != null) {
                    // 방어 코드: parent의 children이 null이면 초기화
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(category);
                }
            }
        }

        return rootList;
    }

    public CategoryDto.Response.CategoryResponse createCategory(CategoryDto.Request.CategoryCreateRequest dto, String tUserUid) {
        User user = userRepository.findById(tUserUid)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        Category category = categoryMapper.toEntity(dto);
        category.setUser(user);

        Category saved = categoryRepository.save(category);
        return categoryMapper.toResponse(saved);
    }

    public CategoryDto.Response.CategoryResponse updateCategory(CategoryDto.Request.CategoryUpdateRequest dto, String tUserUid) {

        User user = userRepository.findById(tUserUid)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        Category category = categoryMapper.toEntity(dto);
        category.setUser(user);

        Category saved = categoryRepository.save(category);
        return categoryMapper.toResponse(saved);
    }

    public String deleteCategory(String categoryUid, String tUserUid) {
        try {
            Category category = categoryRepository.findByCategoryUidAndUser_userUid(categoryUid, tUserUid);

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
