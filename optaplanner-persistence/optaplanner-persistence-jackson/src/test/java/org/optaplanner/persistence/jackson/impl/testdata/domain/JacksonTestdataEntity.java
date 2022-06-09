package org.optaplanner.persistence.jackson.impl.testdata.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;

@PlanningEntity
public class JacksonTestdataEntity extends JacksonTestdataObject {

    public static EntityDescriptor buildEntityDescriptor() {
        SolutionDescriptor solutionDescriptor = JacksonTestdataSolution.buildSolutionDescriptor();
        return solutionDescriptor.findEntityDescriptorOrFail(JacksonTestdataEntity.class);
    }

    public static GenuineVariableDescriptor buildVariableDescriptorForValue() {
        SolutionDescriptor solutionDescriptor = JacksonTestdataSolution.buildSolutionDescriptor();
        EntityDescriptor entityDescriptor = solutionDescriptor.findEntityDescriptorOrFail(JacksonTestdataEntity.class);
        return entityDescriptor.getGenuineVariableDescriptor("value");
    }

    private JacksonTestdataValue value;

    public JacksonTestdataEntity() {
    }

    public JacksonTestdataEntity(String code) {
        super(code);
    }

    public JacksonTestdataEntity(String code, JacksonTestdataValue value) {
        this(code);
        this.value = value;
    }

    @PlanningVariable(valueRangeProviderRefs = "valueRange")
    public JacksonTestdataValue getValue() {
        return value;
    }

    public void setValue(JacksonTestdataValue value) {
        this.value = value;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
