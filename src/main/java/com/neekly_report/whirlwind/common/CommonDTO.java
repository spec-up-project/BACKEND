package com.neekly_report.whirlwind.common;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class CommonDTO {
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
}
