package com.neekly_report.whirlwind.plan;

import com.neekly_report.whirlwind.entity.Plan;

import java.util.List;

public interface PlanServiceImp {
    List<Plan> getPlanList(String tUserUid);
}
