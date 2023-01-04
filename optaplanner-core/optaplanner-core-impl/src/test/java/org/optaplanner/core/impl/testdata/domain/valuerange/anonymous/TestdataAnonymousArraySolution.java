package org.optaplanner.core.impl.testdata.domain.valuerange.anonymous;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;

@PlanningSolution
public class TestdataAnonymousArraySolution extends TestdataObject {

    public static SolutionDescriptor<TestdataAnonymousArraySolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TestdataAnonymousArraySolution.class,
                TestdataAnonymousValueRangeEntity.class);
    }

    private List<TestdataAnonymousValueRangeEntity> entityList;

    private SimpleScore score;

    public TestdataAnonymousArraySolution() {
    }

    public TestdataAnonymousArraySolution(String code) {
        super(code);
    }

    @PlanningEntityCollectionProperty
    public List<TestdataAnonymousValueRangeEntity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<TestdataAnonymousValueRangeEntity> entityList) {
        this.entityList = entityList;
    }

    @PlanningScore
    public SimpleScore getScore() {
        return score;
    }

    public void setScore(SimpleScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @ValueRangeProvider
    public Integer[] createIntegerArray() {
        return new Integer[] { 0, 1 };
    }

    @ValueRangeProvider
    public Long[] createLongArray() {
        return new Long[] { 0L, 1L };
    }

    @ValueRangeProvider
    public Number[] createNumberArray() {
        return new Number[] { 0L, 1L };
    }

    @ValueRangeProvider
    public BigInteger[] createBigIntegerArray() {
        return new BigInteger[] { BigInteger.ZERO, BigInteger.TEN };
    }

    @ValueRangeProvider
    public BigDecimal[] createBigDecimalArray() {
        return new BigDecimal[] { BigDecimal.ZERO, BigDecimal.TEN };
    }

}
