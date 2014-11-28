package org.optaplanner.core.impl.heuristic.selector.entity.decorator;

import org.junit.Test;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.nullable.TestdataNullableEntity;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import static org.junit.Assert.*;

public class NullValueReinitializeVariableEntityFilterTest {

    @Test
    public void accept() {
        EntityDescriptor entityDescriptor = TestdataEntity.buildEntityDescriptor();
        GenuineVariableDescriptor variableDescriptor = entityDescriptor.getGenuineVariableDescriptor("value");
        NullValueReinitializeVariableEntityFilter filter = new NullValueReinitializeVariableEntityFilter(variableDescriptor);
        assertEquals(false, filter.accept(null, new TestdataEntity("a", new TestdataValue())));
        assertEquals(true, filter.accept(null, new TestdataEntity("b", null)));
    }

    @Test
    public void acceptWithNullableEntity() {
        EntityDescriptor entityDescriptor = TestdataNullableEntity.buildEntityDescriptor();
        GenuineVariableDescriptor variableDescriptor = entityDescriptor.getGenuineVariableDescriptor("value");
        NullValueReinitializeVariableEntityFilter filter = new NullValueReinitializeVariableEntityFilter(variableDescriptor);
        assertEquals(false, filter.accept(null, new TestdataNullableEntity("a", new TestdataValue())));
        assertEquals(true, filter.accept(null, new TestdataNullableEntity("b", null)));
    }

}
