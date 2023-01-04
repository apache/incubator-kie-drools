package org.optaplanner.core.impl.testdata.domain.valuerange.anonymous;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.CountableValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRangeFactory;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;

@PlanningSolution
public class TestdataAnonymousValueRangeSolution extends TestdataObject {

    public static SolutionDescriptor<TestdataAnonymousValueRangeSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TestdataAnonymousValueRangeSolution.class,
                TestdataAnonymousValueRangeEntity.class);
    }

    private List<TestdataAnonymousValueRangeEntity> entityList;

    private SimpleScore score;

    public TestdataAnonymousValueRangeSolution() {
    }

    public TestdataAnonymousValueRangeSolution(String code) {
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
    public CountableValueRange<Integer> createIntValueRange() {
        return ValueRangeFactory.createIntValueRange(0, 3);
    }

    @ValueRangeProvider
    public CountableValueRange<Long> createLongValueRange() {
        return ValueRangeFactory.createLongValueRange(1_000L, 1_003L);
    }

    @ValueRangeProvider
    public CountableValueRange<BigInteger> createBigIntegerValueRange() {
        return ValueRangeFactory.createBigIntegerValueRange(
                BigInteger.valueOf(1_000_000L), BigInteger.valueOf(1_000_003L));
    }

    @ValueRangeProvider
    public CountableValueRange<BigDecimal> createBigDecimalValueRange() {
        return ValueRangeFactory.createBigDecimalValueRange(new BigDecimal("0.00"), new BigDecimal("0.03"));
    }

}
