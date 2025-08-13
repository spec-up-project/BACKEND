package com.neekly_report.whirlwind.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_CATEGORY")
public class Category extends Common {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "T_CATEGORY_UID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
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
