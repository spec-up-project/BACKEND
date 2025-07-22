package com.neekly_report.whirlwind.plan;

import com.neekly_report.whirlwind.entity.Plan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanService implements PlanServiceImp{

    private final PlanRepository planRepository;

    @Override
    public List<PlanDTO.PlanResponse> getPlanList(String tUserUid) {

        return planRepository.findAllByUser_tUserUidOrderByPlanFromDateAsc(tUserUid);
    }
}
