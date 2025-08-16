package com.neekly_report.whirlwind.repository;

import com.neekly_report.whirlwind.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, String> {

    List<Category> findByUser_userUidOrderByCreateDateAsc(String userUid);
    
//    Category findByCategoryUidAndUser_userUid(String userUserUid, String categoryUid);

    Category findByCategoryUidAndUser_userUid(String categoryUid, String userUid);
    List<Category> findByUser_userUidAndNameContaining(String userUid, String name);
}