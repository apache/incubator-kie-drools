package org.optaplanner.core.impl.heuristic.selector.entity.decorator;

import org.junit.Test;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.nullable.TestdataNullableEntity;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NullValueReinitializeVariableEntityFilterTest {

    /*
    this method is written too dummy! ask lpetrovi or geoffrey about mocking it somehow
     */
    @Test
    public void accept() throws IntrospectionException {
        EntityDescriptor entityDescriptor = new EntityDescriptor(null, TestdataNullableEntity.class);
        PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(TestdataNullableEntity.class).getPropertyDescriptors();

        for (int i = 0; i < propertyDescriptors.length; i++) {
            if(propertyDescriptors[i].getName().equals("value")) {
                GenuineVariableDescriptor descriptor = new GenuineVariableDescriptor(entityDescriptor, propertyDescriptors[i]);
                NullValueReinitializeVariableEntityFilter filter = new NullValueReinitializeVariableEntityFilter(descriptor);
                assertTrue(filter.accept(null, new TestdataNullableEntity()));
                break;
            }
        }

        entityDescriptor = new EntityDescriptor(null, TestdataEntity.class);
        propertyDescriptors = Introspector.getBeanInfo(TestdataEntity.class).getPropertyDescriptors();

        for (int i = 0; i < propertyDescriptors.length; i++) {
            if(propertyDescriptors[i].getName().equals("value")) {
                GenuineVariableDescriptor descriptor = new GenuineVariableDescriptor(entityDescriptor, propertyDescriptors[i]);
                NullValueReinitializeVariableEntityFilter filter = new NullValueReinitializeVariableEntityFilter(descriptor);
                TestdataEntity object = new TestdataEntity();
                object.setValue(new TestdataValue());
                assertFalse(filter.accept(null, object));
                break;
            }
        }
    }
}
