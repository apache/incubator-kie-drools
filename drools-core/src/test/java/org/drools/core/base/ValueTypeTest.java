package org.drools.core.base;

import org.drools.base.base.ValueType;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ValueTypeTest {

    @Test
    public void testIsBoolean() {
        assertThat(ValueType.BOOLEAN_TYPE.isBoolean()).isTrue();
        assertThat(ValueType.PBOOLEAN_TYPE.isBoolean()).isTrue();
    }

    @Test
    public void testIsNumber() {
        assertThat(ValueType.PBYTE_TYPE.isNumber()).isTrue();
        assertThat(ValueType.PSHORT_TYPE.isNumber()).isTrue();
        assertThat(ValueType.PINTEGER_TYPE.isNumber()).isTrue();
        assertThat(ValueType.PLONG_TYPE.isNumber()).isTrue();
        assertThat(ValueType.PFLOAT_TYPE.isNumber()).isTrue();
        assertThat(ValueType.PDOUBLE_TYPE.isNumber()).isTrue();
        assertThat(ValueType.BYTE_TYPE.isNumber()).isTrue();
        assertThat(ValueType.SHORT_TYPE.isNumber()).isTrue();
        assertThat(ValueType.INTEGER_TYPE.isNumber()).isTrue();
        assertThat(ValueType.LONG_TYPE.isNumber()).isTrue();
        assertThat(ValueType.FLOAT_TYPE.isNumber()).isTrue();
        assertThat(ValueType.DOUBLE_TYPE.isNumber()).isTrue();

    }

}
