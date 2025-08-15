package com.neekly_report.whirlwind.api;

import com.neekly_report.whirlwind.dto.CategoryDto;
import com.neekly_report.whirlwind.dto.UserDto;
import com.neekly_report.whirlwind.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
@Slf4j
public class CategoryApiController {

    private final CategoryService categoryService;

    @Operation(summary = "사용자별 카테고리 조회")
    @GetMapping
    public ResponseEntity<List<CategoryDto.Response.CategoryResponse>> getUserCategories(
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        List<CategoryDto.Response.CategoryResponse> categories = categoryService.getUserCategories(userDetail.getUserUid());
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "세그먼트 타입별 카테고리 조회")
    @GetMapping("/segtype/{segType}")
    public ResponseEntity<List<CategoryDto.Response.CategoryResponse>> getCategoriesBySegType(
            @PathVariable String segType,
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        List<CategoryDto.Response.CategoryResponse> categories = categoryService.getCategoriesBySegType(userDetail.getUserUid(), segType);
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "카테고리 생성 < MAIN : 대분류, SUB: 소분류 >")
    @PostMapping("insert")
    public ResponseEntity<CategoryDto.Response.CategoryResponse> createCategory(
            @RequestBody @Valid CategoryDto.Request.CategoryCreateRequest request,
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        CategoryDto.Response.CategoryResponse category = categoryService.createCategory(userDetail.getUserUid(), request);
        return ResponseEntity.ok(category);
    }

    @Operation(summary = "카테고리 수정")
    @PutMapping("/update")
    public ResponseEntity<CategoryDto.Response.CategoryResponse> updateCategory(
            @RequestBody @Valid CategoryDto.Request.CategoryUpdateRequest request,
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        CategoryDto.Response.CategoryResponse category = categoryService.updateCategory(userDetail.getUserUid(), request);
        return ResponseEntity.ok(category);
    }

    @Operation(summary = "카테고리 삭제")
    @DeleteMapping("/delete/{categoryUid}")
    public ResponseEntity<String> deleteCategory(
            @PathVariable String categoryUid,
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        String result = categoryService.deleteCategory(userDetail.getUserUid(), categoryUid);
        return ResponseEntity.ok(result);
    }
}