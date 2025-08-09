package com.neekly_report.whirlwind.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "T_CATEGORY")
public class Category extends Common {

    @Id
    @Column(name = "T_CATEGORY_UID", nullable = false, updatable = false)
    private String tCategoryUid;

    @Column(name = "CATEGORY_NAME")
    @Comment("분류 명")
    private String categoryName;

    @Column(name = "SEG_TYPE")
    @Comment("분류 구분")
    private String segType;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;
}
