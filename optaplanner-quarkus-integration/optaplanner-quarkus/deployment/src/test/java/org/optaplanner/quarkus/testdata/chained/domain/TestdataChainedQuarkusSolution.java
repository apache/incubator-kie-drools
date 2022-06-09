package org.optaplanner.quarkus.testdata.chained.domain;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;

@PlanningSolution
public class TestdataChainedQuarkusSolution {

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "chainedAnchorRange")
    private List<TestdataChainedQuarkusAnchor> chainedAnchorList;
    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "chainedEntityRange")
    private List<TestdataChainedQuarkusEntity> chainedEntityList;

    @PlanningScore
    private SimpleScore score;

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public List<TestdataChainedQuarkusAnchor> getChainedAnchorList() {
        return chainedAnchorList;
    }

    public void setChainedAnchorList(List<TestdataChainedQuarkusAnchor> chainedAnchorList) {
        this.chainedAnchorList = chainedAnchorList;
    }

    public List<TestdataChainedQuarkusEntity> getChainedEntityList() {
        return chainedEntityList;
    }

    public void setChainedEntityList(List<TestdataChainedQuarkusEntity> chainedEntityList) {
        this.chainedEntityList = chainedEntityList;
    }

    public SimpleScore getScore() {
        return score;
    }

    public void setScore(SimpleScore score) {
        this.score = score;
    }

}
