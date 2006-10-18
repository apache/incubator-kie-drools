package org.drools.base;

import junit.framework.Assert;
import junit.framework.TestCase;

public class ValueTypeTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testIsBoolean() {
        Assert.assertTrue(ValueType.BOOLEAN_TYPE.isBoolean());
        Assert.assertTrue(ValueType.PBOOLEAN_TYPE.isBoolean());
    }

    public void testIsNumber() {
        Assert.assertTrue(ValueType.PBYTE_TYPE.isNumber());
        Assert.assertTrue(ValueType.PSHORT_TYPE.isNumber());
        Assert.assertTrue(ValueType.PINTEGER_TYPE.isNumber());
        Assert.assertTrue(ValueType.PLONG_TYPE.isNumber());
        Assert.assertTrue(ValueType.PFLOAT_TYPE.isNumber());
        Assert.assertTrue(ValueType.PDOUBLE_TYPE.isNumber());
        Assert.assertTrue(ValueType.BYTE_TYPE.isNumber());
        Assert.assertTrue(ValueType.SHORT_TYPE.isNumber());
        Assert.assertTrue(ValueType.INTEGER_TYPE.isNumber());
        Assert.assertTrue(ValueType.LONG_TYPE.isNumber());
        Assert.assertTrue(ValueType.FLOAT_TYPE.isNumber());
        Assert.assertTrue(ValueType.DOUBLE_TYPE.isNumber());
        
    }

}
