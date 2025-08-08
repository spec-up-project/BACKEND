package com.neekly_report.whirlwind.dto;

import com.neekly_report.whirlwind.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private String categoryName;
    private String segType; // "MAIN", "SUB"
    private User user;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
}
