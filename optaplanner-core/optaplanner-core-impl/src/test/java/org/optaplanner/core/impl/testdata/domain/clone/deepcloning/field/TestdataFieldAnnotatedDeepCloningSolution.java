package org.optaplanner.core.impl.testdata.domain.clone.deepcloning.field;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.cloner.DeepPlanningClone;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningSolution
public class TestdataFieldAnnotatedDeepCloningSolution extends TestdataObject {

    public static SolutionDescriptor<TestdataFieldAnnotatedDeepCloningSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TestdataFieldAnnotatedDeepCloningSolution.class,
                TestdataFieldAnnotatedDeepCloningEntity.class);
    }

    @ValueRangeProvider(id = "valueRange")
    @ProblemFactCollectionProperty
    private List<TestdataValue> valueList;
    @PlanningEntityCollectionProperty
    private List<TestdataFieldAnnotatedDeepCloningEntity> entityList;
    @DeepPlanningClone
    private List<String> generalShadowVariableList;

    @PlanningScore
    private SimpleScore score;

    public TestdataFieldAnnotatedDeepCloningSolution() {
    }

    public TestdataFieldAnnotatedDeepCloningSolution(String code) {
        super(code);
    }

    public List<TestdataValue> getValueList() {
        return valueList;
    }

    public void setValueList(List<TestdataValue> valueList) {
        this.valueList = valueList;
    }

    public List<TestdataFieldAnnotatedDeepCloningEntity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<TestdataFieldAnnotatedDeepCloningEntity> entityList) {
        this.entityList = entityList;
    }

    public List<String> getGeneralShadowVariableList() {
        return generalShadowVariableList;
    }

    public void setGeneralShadowVariableList(List<String> generalShadowVariableList) {
        this.generalShadowVariableList = generalShadowVariableList;
    }

    public SimpleScore getScore() {
        return score;
    }

    public void setScore(SimpleScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
