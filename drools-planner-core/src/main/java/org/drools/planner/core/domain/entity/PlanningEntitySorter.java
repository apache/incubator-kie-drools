package org.drools.planner.core.domain.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.drools.planner.core.solution.Solution;

/**
 * Helper class for {@link PlanningEntityDifficultyWeightFactory}.
 * @see PlanningEntityDifficultyWeightFactory
 */
public class PlanningEntitySorter {

    private Comparator<Object> difficultyComparator = null;
    private PlanningEntityDifficultyWeightFactory difficultyWeightFactory = null;

    public PlanningEntitySorter() {
    }

    public void setDifficultyComparator(Comparator<Object> difficultyComparator) {
        this.difficultyComparator = difficultyComparator;
    }

    public void setDifficultyWeightFactory(PlanningEntityDifficultyWeightFactory difficultyWeightFactory) {
        this.difficultyWeightFactory = difficultyWeightFactory;
    }

    public boolean isSortDifficultySupported() {
        return difficultyComparator != null || difficultyWeightFactory != null;
    }

    public void sortDifficultyAscending(Solution solution, List<Object> planningEntityList) {
        if (difficultyComparator != null) {
            Collections.sort(planningEntityList, difficultyComparator);
        } else if (difficultyWeightFactory != null) {
            SortedMap<Comparable, Object> planningEntityMap = new TreeMap<Comparable, Object>();
            for (Object planningEntity : planningEntityList) {
                Comparable difficultyWeight = difficultyWeightFactory.createDifficultyWeight(solution, planningEntity);
                Object previous = planningEntityMap.put(difficultyWeight, planningEntity);
                if (previous != null) {
                    throw new IllegalStateException("The planningEntityList contains 2 times the same planningEntity ("
                            + previous + ") and (" + planningEntity + ").");
                }
            }
            planningEntityList.clear();
            planningEntityList.addAll(planningEntityMap.values());
        } else {
            throw new IllegalStateException("sortDifficultyDescending is impossible" +
                    " because difficultyComparator and difficultyWeightFactory are null.");
        }
    }

    public void sortDifficultyDescending(Solution solution, List<Object> planningEntityList) {
        sortDifficultyAscending(solution, planningEntityList);
        Collections.reverse(planningEntityList);
    }

}
