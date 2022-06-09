package org.optaplanner.persistence.xstream.impl.testdata.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@PlanningEntity
@XStreamAlias("xStreamTestdataEntity")
public class XStreamTestdataEntity extends TestdataObject {

    public static EntityDescriptor buildEntityDescriptor() {
        SolutionDescriptor solutionDescriptor = XStreamTestdataSolution.buildSolutionDescriptor();
        return solutionDescriptor.findEntityDescriptorOrFail(XStreamTestdataEntity.class);
    }

    public static GenuineVariableDescriptor buildVariableDescriptorForValue() {
        SolutionDescriptor solutionDescriptor = XStreamTestdataSolution.buildSolutionDescriptor();
        EntityDescriptor entityDescriptor = solutionDescriptor.findEntityDescriptorOrFail(XStreamTestdataEntity.class);
        return entityDescriptor.getGenuineVariableDescriptor("value");
    }

    private XStreamTestdataValue value;

    public XStreamTestdataEntity() {
    }

    public XStreamTestdataEntity(String code) {
        super(code);
    }

    public XStreamTestdataEntity(String code, XStreamTestdataValue value) {
        this(code);
        this.value = value;
    }

    @PlanningVariable(valueRangeProviderRefs = "valueRange")
    public XStreamTestdataValue getValue() {
        return value;
    }

    public void setValue(XStreamTestdataValue value) {
        this.value = value;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
