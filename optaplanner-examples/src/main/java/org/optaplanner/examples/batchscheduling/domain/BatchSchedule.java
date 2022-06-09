package org.optaplanner.examples.batchscheduling.domain;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.persistence.xstream.api.score.buildin.bendablelong.BendableLongScoreXStreamConverter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@PlanningSolution
@XStreamAlias("PipeSchedule")
public class BatchSchedule extends AbstractPersistable {

    private List<Batch> batchList;

    private List<Allocation> allocationList;
    private List<AllocationPath> allocationPathList;

    @XStreamConverter(BendableLongScoreXStreamConverter.class)
    private BendableLongScore score;

    @ProblemFactCollectionProperty
    public List<Batch> getBatchList() {
        return batchList;
    }

    public void setBatchList(List<Batch> batchList) {
        this.batchList = batchList;
    }

    @PlanningEntityCollectionProperty
    public List<Allocation> getAllocationList() {
        return allocationList;
    }

    public void setAllocationList(List<Allocation> allocationList) {
        this.allocationList = allocationList;
    }

    @PlanningEntityCollectionProperty
    public List<AllocationPath> getAllocationPathList() {
        return allocationPathList;
    }

    public void setAllocationPathList(List<AllocationPath> allocationPathList) {
        this.allocationPathList = allocationPathList;
    }

    @PlanningScore(bendableHardLevelsSize = 3, bendableSoftLevelsSize = 2)
    public BendableLongScore getScore() {
        return score;
    }

    public void setScore(BendableLongScore score) {
        this.score = score;
    }

}
