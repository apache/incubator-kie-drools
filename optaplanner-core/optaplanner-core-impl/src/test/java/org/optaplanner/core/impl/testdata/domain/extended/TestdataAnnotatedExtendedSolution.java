package org.optaplanner.core.impl.testdata.domain.extended;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningSolution
public class TestdataAnnotatedExtendedSolution extends TestdataSolution {

    public static SolutionDescriptor<TestdataAnnotatedExtendedSolution> buildExtendedSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TestdataAnnotatedExtendedSolution.class,
                TestdataEntity.class, TestdataAnnotatedExtendedEntity.class);
    }

    private List<TestdataValue> subValueList;

    private List<TestdataAnnotatedExtendedEntity> subEntityList;

    public TestdataAnnotatedExtendedSolution() {
    }

    public TestdataAnnotatedExtendedSolution(String code) {
        super(code);
    }

    @ValueRangeProvider(id = "subValueRange")
    @ProblemFactCollectionProperty
    public List<TestdataValue> getSubValueList() {
        return subValueList;
    }

    public void setSubValueList(List<TestdataValue> subValueList) {
        this.subValueList = subValueList;
    }

    @PlanningEntityCollectionProperty
    public List<TestdataAnnotatedExtendedEntity> getSubEntityList() {
        return subEntityList;
    }

    public void setSubEntityList(List<TestdataAnnotatedExtendedEntity> subEntityList) {
        this.subEntityList = subEntityList;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
