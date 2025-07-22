package com.neekly_report.whirlwind.plan;


import com.neekly_report.whirlwind.common.CommonDTO;
import com.neekly_report.whirlwind.entity.Plan;
import lombok.*;

import java.util.Date;

public class PlanDTO {

    @Data
    @Builder
    @AllArgsConstructor
    public static class PlanRequest {
        private String tUserUid;
        private String tPlanUid;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class PlanResponse extends CommonDTO {
        private String tUserUid;
        private String tPlanUid;
        private String planTitle;
        private String planContent;
        private Date planFromDate;
        private Date planEndDate;
    }
}
