package org.drools.planner.core.domain.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.drools.planner.core.solution.Solution;

/**
 * Helper class for {@link PlanningEntityDifficultyWeightFactory}.
 * @see PlanningEntityDifficultyWeightFactory
 */
public class PlanningEntityDifficultyWeightUtils {

    public static List<Object> sortByDifficultyWeight(Solution solution, Collection<Object> planningEntities,
            PlanningEntityDifficultyWeightFactory planningEntityDifficultyWeightFactory, boolean ascending) {
        SortedMap<Comparable, Object> planningEntityMap = new TreeMap<Comparable, Object>();
        for (Object planningEntity : planningEntities) {
            Comparable difficultyWeight = planningEntityDifficultyWeightFactory
                    .createDifficultyWeight(solution, planningEntity);
            planningEntityMap.put(difficultyWeight, planningEntity);
        }
        ArrayList<Object> ordedPlanningEntities = new ArrayList<Object>(planningEntityMap.values());
        if (!ascending) {
            Collections.reverse(ordedPlanningEntities);
        }
        return ordedPlanningEntities;
    }

    private PlanningEntityDifficultyWeightUtils() {
    }

}
