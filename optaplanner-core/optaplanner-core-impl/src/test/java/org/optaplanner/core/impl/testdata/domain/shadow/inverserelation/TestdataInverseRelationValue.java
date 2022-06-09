package org.optaplanner.core.impl.testdata.domain.shadow.inverserelation;

import java.util.ArrayList;
import java.util.Collection;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;

@PlanningEntity
public class TestdataInverseRelationValue extends TestdataObject {

    private Collection<TestdataInverseRelationEntity> entities = new ArrayList<>();

    public TestdataInverseRelationValue() {
    }

    public TestdataInverseRelationValue(String code) {
        super(code);
    }

    @InverseRelationShadowVariable(sourceVariableName = "value")
    public Collection<TestdataInverseRelationEntity> getEntities() {
        return entities;
    }

    public void setEntities(Collection<TestdataInverseRelationEntity> entities) {
        this.entities = entities;
    }

}
