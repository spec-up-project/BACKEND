package com.neekly_report.whirlwind.repository;

import com.neekly_report.whirlwind.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, String> {

    List<Category> findByUser_tUserUid(String tUserUid);

    List<Category> findByUser_tUserUidAndSegType(String tUserUid, String segType);

    @Query("SELECT c FROM Category c WHERE c.user.tUserUid = :userTUserUid AND c.tCategoryUid = :tCategoryUid")
    Category findByTCategoryUidAndUser_tUserUid(@Param("userTUserUid") String userTUserUid, @Param("tCategoryUid") String tCategoryUid);

    List<Category> findByUser_tUserUidAndCategoryNameContaining(String tUserUid, String categoryName);
}