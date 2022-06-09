package org.optaplanner.core.impl.testdata.domain.list;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.IndexShadowVariable;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.index.IndexShadowVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.InverseRelationShadowVariableDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;

@PlanningEntity
public class TestdataListValue extends TestdataObject {

    public static EntityDescriptor<TestdataListSolution> buildEntityDescriptor() {
        return TestdataListSolution.buildSolutionDescriptor().findEntityDescriptorOrFail(TestdataListValue.class);
    }

    public static InverseRelationShadowVariableDescriptor<TestdataListSolution> buildVariableDescriptorForEntity() {
        return (InverseRelationShadowVariableDescriptor<TestdataListSolution>) buildEntityDescriptor()
                .getShadowVariableDescriptor("entity");
    }

    public static IndexShadowVariableDescriptor<TestdataListSolution> buildVariableDescriptorForIndex() {
        return (IndexShadowVariableDescriptor<TestdataListSolution>) buildEntityDescriptor()
                .getShadowVariableDescriptor("index");
    }

    @InverseRelationShadowVariable(sourceVariableName = "valueList")
    private TestdataListEntity entity;
    @IndexShadowVariable(sourceVariableName = "valueList")
    private Integer index;

    public TestdataListValue() {
    }

    public TestdataListValue(String code) {
        super(code);
    }

    public TestdataListEntity getEntity() {
        return entity;
    }

    public void setEntity(TestdataListEntity entity) {
        this.entity = entity;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
