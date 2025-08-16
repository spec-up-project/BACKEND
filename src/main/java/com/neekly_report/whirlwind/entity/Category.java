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
    @Comment("카테고리 ID")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "T_CATEGORY_UID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
    private String categoryUid;

    @Column(name = "PARENT_UID", columnDefinition = "CHAR(36)")
    @Comment("상위 카테고리 ID")
    private String parentUid;

    @Column(name = "CATEGORY_NAME")
    @Comment("카테고리 명")
    private String name;

    @Column(name = "DEPTH", columnDefinition = "SMALLINT")
    @Comment("카테고리 DEPTH")
    private String depth;

    @ManyToOne
    @JoinColumn(name = "T_USER_UID")
    private User user;
}
