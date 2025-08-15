package com.neekly_report.whirlwind.repository;

import com.neekly_report.whirlwind.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, String> {

    List<Category> findByUser_userUid(String userUid);

    List<Category> findByUser_userUidAndSegType(String userUid, String segType);
    
//    Category findByCategoryUidAndUser_userUid(String userUserUid, String categoryUid);

    Category findByCategoryUidAndUser_userUid(String categoryUid, String userUid);
    List<Category> findByUser_userUidAndCategoryNameContaining(String userUid, String categoryName);
}