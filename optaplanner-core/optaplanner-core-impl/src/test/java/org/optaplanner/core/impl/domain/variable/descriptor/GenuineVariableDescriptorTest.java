package org.optaplanner.core.impl.domain.variable.descriptor;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListEntity;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListValue;
import org.optaplanner.core.impl.testdata.domain.nullable.TestdataNullableEntity;

class GenuineVariableDescriptorTest {

    @Test
    void isReinitializable() {
        GenuineVariableDescriptor<?> variableDescriptor = TestdataEntity.buildVariableDescriptorForValue();
        assertThat(variableDescriptor.isReinitializable(new TestdataEntity("a", new TestdataValue()))).isFalse();
        assertThat(variableDescriptor.isReinitializable(new TestdataEntity("b", null))).isTrue();
    }

    @Test
    void isReinitializable_nullable() {
        GenuineVariableDescriptor<?> variableDescriptor = TestdataNullableEntity.buildVariableDescriptorForValue();
        assertThat(variableDescriptor.isReinitializable(new TestdataNullableEntity("a", new TestdataValue()))).isFalse();
        assertThat(variableDescriptor.isReinitializable(new TestdataNullableEntity("b", null))).isTrue();
    }

    @Test
    void isReinitializable_list() {
        GenuineVariableDescriptor<?> variableDescriptor = TestdataListEntity.buildVariableDescriptorForValueList();
        assertThat(variableDescriptor.isReinitializable(new TestdataListEntity("a", new TestdataListValue()))).isFalse();
        assertThat(variableDescriptor.isReinitializable(new TestdataListEntity("b", new ArrayList<>()))).isFalse();
    }
}
