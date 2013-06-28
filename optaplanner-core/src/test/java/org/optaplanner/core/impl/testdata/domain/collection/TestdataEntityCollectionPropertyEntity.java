package org.optaplanner.core.impl.testdata.domain.collection;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.value.ValueRange;
import org.optaplanner.core.api.domain.value.ValueRangeType;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.domain.entity.PlanningEntityDescriptor;
import org.optaplanner.core.impl.domain.solution.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity
public class TestdataEntityCollectionPropertyEntity extends TestdataObject {

    public static PlanningEntityDescriptor buildEntityDescriptor() {
        SolutionDescriptor solutionDescriptor = TestdataEntityCollectionPropertySolution.buildSolutionDescriptor();
        return solutionDescriptor.getEntityDescriptor(TestdataEntityCollectionPropertyEntity.class);
    }

    private List<TestdataEntityCollectionPropertyEntity> entityList;
    private Set<TestdataEntityCollectionPropertyEntity> entitySet;
    private Map<String, TestdataEntityCollectionPropertyEntity> entityMap;

    private TestdataValue value;

    public TestdataEntityCollectionPropertyEntity() {
    }

    public TestdataEntityCollectionPropertyEntity(String code) {
        super(code);
    }

    public TestdataEntityCollectionPropertyEntity(String code, TestdataValue value) {
        this(code);
        this.value = value;
    }

    public List<TestdataEntityCollectionPropertyEntity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<TestdataEntityCollectionPropertyEntity> entityList) {
        this.entityList = entityList;
    }

    public Set<TestdataEntityCollectionPropertyEntity> getEntitySet() {
        return entitySet;
    }

    public void setEntitySet(Set<TestdataEntityCollectionPropertyEntity> entitySet) {
        this.entitySet = entitySet;
    }

    public Map<String, TestdataEntityCollectionPropertyEntity> getEntityMap() {
        return entityMap;
    }

    public void setEntityMap(Map<String, TestdataEntityCollectionPropertyEntity> entityMap) {
        this.entityMap = entityMap;
    }

    @PlanningVariable
    @ValueRange(type = ValueRangeType.FROM_SOLUTION_PROPERTY, solutionProperty = "valueList")
    public TestdataValue getValue() {
        return value;
    }

    public void setValue(TestdataValue value) {
        this.value = value;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
