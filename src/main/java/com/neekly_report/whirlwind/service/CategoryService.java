
package com.neekly_report.whirlwind.service;

import com.neekly_report.whirlwind.dto.CategoryDto;
import com.neekly_report.whirlwind.entity.Category;
import com.neekly_report.whirlwind.entity.User;
import com.neekly_report.whirlwind.mapper.CategoryMapper;
import com.neekly_report.whirlwind.repository.CategoryRepository;
import com.neekly_report.whirlwind.repository.UserRepository;
import jakarta.transaction.Transactional;
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

    public List<CategoryDto.Response.CategoryResponse> getUserCategories(String userUid) {
        List<Category> categoryList = categoryRepository.findByUser_userUidOrderByCreateDateAsc(userUid);
        List<CategoryDto.Response.CategoryResponse> categoryResponses = categoryList.stream().map(categoryMapper::toResponse).toList();

        return buildCategoryTree(categoryResponses);
    }

    public List<CategoryDto.Response.CategoryResponse> buildCategoryTree(List<CategoryDto.Response.CategoryResponse> flatList) {
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

    public CategoryDto.Response.CategoryResponse createCategory(CategoryDto.Request.CategoryCreateRequest dto, String userUid) {
        User user = userRepository.findById(userUid)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        Category category = categoryMapper.toEntity(dto);
        category.setUser(user);

        Category saved = categoryRepository.save(category);
        return categoryMapper.toResponse(saved);
    }

    public CategoryDto.Response.CategoryResponse updateCategory(CategoryDto.Request.CategoryUpdateRequest dto, String userUid) {

        User user = userRepository.findById(userUid)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        Category category = categoryMapper.toEntity(dto);
        category.setUser(user);

        Category saved = categoryRepository.save(category);
        return categoryMapper.toResponse(saved);
    }

    @Transactional
    public String deleteCategory(String categoryUid, String userUid) {
        try {
            User user = userRepository.findById(userUid)
                    .orElseThrow(() -> new RuntimeException("사용자 없음"));

            List<Category> categoryList = categoryRepository.findByUser_userUidOrderByCreateDateAsc(userUid);
            List<CategoryDto.Response.CategoryResponse> categoryResponses = categoryList.stream().map(categoryMapper::toResponse).toList();

            List<CategoryDto.Response.CategoryResponse> deleteList = findAllDescendants(categoryUid, categoryResponses);

            for (CategoryDto.Response.CategoryResponse delete : deleteList) {
                Category category = categoryMapper.toEntity(delete);
                category.setUser(user);
                categoryRepository.delete(category);
            }

            return categoryUid;
        } catch (Exception e) {
            return e.getStackTrace()[0].toString() + " : " + e.getMessage() + " : " + categoryUid;
        }
    }

    /**
     * 주어진 상위 categoryUid를 기준으로 모든 하위 카테고리를 재귀적으로 탐색
     *
     * @param parentUid 상위 카테고리의 UID
     * @param flatList 전체 카테고리 리스트 (트리 구조가 아닌 평탄화된 리스트)
     * @return 하위 카테고리들을 포함한 리스트 (자기 자신 포함 가능)
     */
    public List<CategoryDto.Response.CategoryResponse> findAllDescendants(String parentUid, List<CategoryDto.Response.CategoryResponse> flatList) {
        // 1. 자식 카테고리를 빠르게 찾기 위한 맵 생성
        Map<String, List<CategoryDto.Response.CategoryResponse>> childrenMap = new HashMap<>();

        for (CategoryDto.Response.CategoryResponse category : flatList) {
            String pid = category.getParentUid();
            if (pid != null && !pid.isEmpty()) {
                // 부모 UID를 기준으로 자식 리스트를 맵에 저장
                childrenMap.computeIfAbsent(pid, k -> new ArrayList<>()).add(category);
            }
        }

        // 2. 결과를 담을 리스트 생성
        List<CategoryDto.Response.CategoryResponse> result = new ArrayList<>();


        // 3. 자기 자신 먼저 추가
        flatList.stream().filter(c -> c.getCategoryUid().equals(parentUid)).findFirst().ifPresent(result::add);

        // 4. 재귀적으로 자식들을 탐색하여 결과 리스트에 추가
        findChildrenRecursive(parentUid, childrenMap, result);

        return result;
    }

    /**
     * 재귀적으로 자식 카테고리를 탐색하여 결과 리스트에 추가하는 메서드
     *
     * @param parentUid 현재 탐색 중인 부모 UID
     * @param childrenMap 부모 UID → 자식 리스트 맵
     * @param result 결과 리스트에 자식들을 추가
     */
    private void findChildrenRecursive(String parentUid, Map<String, List<CategoryDto.Response.CategoryResponse>> childrenMap, List<CategoryDto.Response.CategoryResponse> result) {
        List<CategoryDto.Response.CategoryResponse> children = childrenMap.get(parentUid);

        if (children != null) {
            for (CategoryDto.Response.CategoryResponse child : children) {
                result.add(child); // 현재 자식 추가
                // 자식의 자식도 재귀적으로 탐색
                findChildrenRecursive(child.getCategoryUid(), childrenMap, result);
            }
        }
    }

}
