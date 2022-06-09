package org.optaplanner.core.impl.testdata.domain.solutionproperties;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.ProblemFactProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningSolution
public class TestdataProblemFactPropertySolution extends TestdataObject {

    public static SolutionDescriptor<TestdataProblemFactPropertySolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TestdataProblemFactPropertySolution.class, TestdataEntity.class);
    }

    private List<TestdataValue> valueList;
    private List<Object> otherProblemFactList;
    private Object extraObject;
    private List<TestdataEntity> entityList;

    private SimpleScore score;

    public TestdataProblemFactPropertySolution() {
    }

    public TestdataProblemFactPropertySolution(String code) {
        super(code);
    }

    @ValueRangeProvider(id = "valueRange")
    @ProblemFactCollectionProperty
    public List<TestdataValue> getValueList() {
        return valueList;
    }

    public void setValueList(List<TestdataValue> valueList) {
        this.valueList = valueList;
    }

    @ProblemFactCollectionProperty
    public List<Object> getOtherProblemFactList() {
        return otherProblemFactList;
    }

    public void setOtherProblemFactList(List<Object> otherProblemFactList) {
        this.otherProblemFactList = otherProblemFactList;
    }

    @ProblemFactProperty
    public Object getExtraObject() {
        return extraObject;
    }

    public void setExtraObject(Object extraObject) {
        this.extraObject = extraObject;
    }

    @PlanningEntityCollectionProperty
    public List<TestdataEntity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<TestdataEntity> entityList) {
        this.entityList = entityList;
    }

    @PlanningScore
    public SimpleScore getScore() {
        return score;
    }

    public void setScore(SimpleScore score) {
        this.score = score;
    }

}
