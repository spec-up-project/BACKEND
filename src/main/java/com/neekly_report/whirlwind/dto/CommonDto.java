package com.neekly_report.whirlwind.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommonDto {
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
}
