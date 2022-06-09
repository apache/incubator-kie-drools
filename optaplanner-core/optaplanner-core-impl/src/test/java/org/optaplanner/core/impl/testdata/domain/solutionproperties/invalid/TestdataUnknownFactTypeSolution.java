package org.optaplanner.core.impl.testdata.domain.solutionproperties.invalid;

import java.util.Collection;
import java.util.List;

import org.optaplanner.core.api.domain.autodiscover.AutoDiscoverMemberType;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningSolution(autoDiscoverMemberType = AutoDiscoverMemberType.FIELD)
public class TestdataUnknownFactTypeSolution extends TestdataObject {

    public static SolutionDescriptor<TestdataUnknownFactTypeSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TestdataUnknownFactTypeSolution.class,
                TestdataEntity.class);
    }

    private List<TestdataValue> valueList;
    private List<TestdataEntity> entityList;
    private SimpleScore score;
    // this can't work with autodiscovery because it's difficult/impossible to resolve the type of collection elements
    private MyStringCollection facts;

    public TestdataUnknownFactTypeSolution() {
    }

    public TestdataUnknownFactTypeSolution(String code) {
        super(code);
    }

    @ValueRangeProvider(id = "valueRange")
    public List<TestdataValue> getValueList() {
        return valueList;
    }

    public static interface MyStringCollection extends Collection<String> {

    }
}
