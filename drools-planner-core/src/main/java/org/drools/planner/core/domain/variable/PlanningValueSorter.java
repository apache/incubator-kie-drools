package org.drools.planner.core.domain.variable;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.drools.planner.api.domain.variable.PlanningValueStrengthWeightFactory;
import org.drools.planner.core.solution.Solution;

/**
 * Helper class for {@link PlanningValueStrengthWeightFactory}.
 * @see PlanningValueStrengthWeightFactory
 */
public class PlanningValueSorter {

    // TODO also keep PlanningEntityDescriptor and the valueRange descriptor?

    private Comparator<Object> strengthComparator = null;
    private PlanningValueStrengthWeightFactory strengthWeightFactory = null;

    public PlanningValueSorter() {
    }

    public void setStrengthComparator(Comparator<Object> strengthComparator) {
        this.strengthComparator = strengthComparator;
    }

    public void setStrengthWeightFactory(PlanningValueStrengthWeightFactory strengthWeightFactory) {
        this.strengthWeightFactory = strengthWeightFactory;
    }

    public boolean isSortStrengthSupported() {
        return strengthComparator != null || strengthWeightFactory != null;
    }

    public void sortStrengthAscending(Solution solution, List<Object> planningValueList) {
        if (strengthComparator != null) {
            Collections.sort(planningValueList, strengthComparator);
        } else if (strengthWeightFactory != null) {
            SortedMap<Comparable, Object> planningValueMap = new TreeMap<Comparable, Object>();
            for (Object planningValue : planningValueList) {
                Comparable strengthWeight = strengthWeightFactory.createStrengthWeight(solution, planningValue);
                Object previous = planningValueMap.put(strengthWeight, planningValue);
                if (previous != null) {
                    throw new IllegalStateException("The planningValueList contains 2 times the same planningValue ("
                            + previous + ") and (" + planningValue + ").");
                }
            }
            planningValueList.clear();
            planningValueList.addAll(planningValueMap.values());
        } else {
            throw new IllegalStateException("Sorting on strength is impossible" +
                    " because strengthComparator and strengthWeightFactory are null.");
        }
    }

    public void sortStrengthDescending(Solution solution, List<Object> planningEntityList) {
        sortStrengthAscending(solution, planningEntityList);
        Collections.reverse(planningEntityList);
    }

}
