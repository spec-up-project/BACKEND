package com.neekly_report.whirlwind.plan;

import com.neekly_report.whirlwind.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, String> {

    List<Plan> findAllByUser_tUserUidOrderByPlanFromDateAsc(String tUserUid);
}
