package org.drools.base.evaluators;

import junit.framework.TestCase;
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.FieldValue;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.InternalFactHandle;
import org.drools.base.ValueType;
import org.drools.rule.VariableRestriction;
import org.drools.rule.Declaration;
import org.drools.reteoo.LeftTuple;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author Michael Neale
 */
public class BigDecimalEqualityTest extends TestCase {

    public void testEquality() {
        EqualityEvaluatorsDefinition.BigDecimalEqualEvaluator d = new EqualityEvaluatorsDefinition.BigDecimalEqualEvaluator();


        assertTrue(d.evaluate(null, new MockInternalReadAcessor(new BigDecimal("42")), null, new MockFieldValue(new BigDecimal("42")) ));
        assertFalse(d.evaluate(null, new MockInternalReadAcessor(new BigDecimal("42")), null, new MockFieldValue(new BigDecimal("43")) ));

        assertTrue(d.evaluate(null, new MockInternalReadAcessor(new BigDecimal("42.0")), null, new MockFieldValue(new BigDecimal("42")) ));
        assertFalse(d.evaluate(null, new MockInternalReadAcessor(new BigDecimal("42")), null, new MockFieldValue(new BigDecimal("43.0")) ));



        assertTrue(d.isEqual(new BigDecimal("42"), new BigDecimal("42")));
        assertFalse(d.isEqual(new BigDecimal("42"), new BigDecimal("43")));
        assertFalse(d.isEqual(new BigDecimal("42"), 43));
        assertFalse(d.isEqual(new BigDecimal("42"), "43"));
        assertFalse(d.isEqual(new BigDecimal("42"), 42.0));

        assertFalse(d.isEqual(43, 43));
        assertFalse(d.isEqual("43", new BigDecimal("43")));


    }

    public void testNotEquals() {
       EqualityEvaluatorsDefinition.BigDecimalNotEqualEvaluator d = new EqualityEvaluatorsDefinition.BigDecimalNotEqualEvaluator();
        assertFalse(d.evaluate(null, new MockInternalReadAcessor(new BigDecimal("42")), null, new MockFieldValue(new BigDecimal("42")) ));
        assertTrue(d.evaluate(null, new MockInternalReadAcessor(new BigDecimal("42")), null, new MockFieldValue(new BigDecimal("43")) ));

        assertFalse(d.evaluate(null, new MockInternalReadAcessor(new BigDecimal("42.0")), null, new MockFieldValue(new BigDecimal("42")) ));
        assertTrue(d.evaluate(null, new MockInternalReadAcessor(new BigDecimal("42")), null, new MockFieldValue(new BigDecimal("43.0")) ));



    }





    class MockFieldValue implements FieldValue {
        private BigDecimal val;

        MockFieldValue(BigDecimal bd) {
            this.val = bd;
        }

        public Object getValue() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public char getCharValue() {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public BigDecimal getBigDecimalValue() {
            return val; 
        }

        public BigInteger getBigIntegerValue() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public int getIntValue() {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public byte getByteValue() {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public short getShortValue() {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public long getLongValue() {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public float getFloatValue() {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public double getDoubleValue() {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public boolean getBooleanValue() {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public boolean isNull() {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public boolean isBooleanField() {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public boolean isIntegerNumberField() {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public boolean isFloatNumberField() {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public boolean isObjectField() {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public boolean isCollectionField() {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public boolean isStringField() {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    class MockInternalReadAcessor implements InternalReadAccessor {
        private Object val;

        MockInternalReadAcessor(Object val) {
            this.val = val;
        }

        public Object getValue(InternalWorkingMemory workingMemory, Object object) {
            return val;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public char getCharValue(InternalWorkingMemory workingMemory, Object object) {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public int getIntValue(InternalWorkingMemory workingMemory, Object object) {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public byte getByteValue(InternalWorkingMemory workingMemory, Object object) {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public short getShortValue(InternalWorkingMemory workingMemory, Object object) {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public long getLongValue(InternalWorkingMemory workingMemory, Object object) {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public float getFloatValue(InternalWorkingMemory workingMemory, Object object) {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public double getDoubleValue(InternalWorkingMemory workingMemory, Object object) {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public boolean getBooleanValue(InternalWorkingMemory workingMemory, Object object) {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public boolean isNullValue(InternalWorkingMemory workingMemory, Object object) {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public int getHashCode(InternalWorkingMemory workingMemory, Object object) {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public boolean isGlobal() {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public boolean isSelfReference() {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public Object getValue(Object object) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public char getCharValue(Object object) {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public int getIntValue(Object object) {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public byte getByteValue(Object object) {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public short getShortValue(Object object) {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public long getLongValue(Object object) {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public float getFloatValue(Object object) {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public double getDoubleValue(Object object) {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public boolean getBooleanValue(Object object) {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public boolean isNullValue(Object object) {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public ValueType getValueType() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public Class<?> getExtractToClass() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public String getExtractToClassName() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public Method getNativeReadMethod() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public int getHashCode(Object object) {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public int getIndex() {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public BigDecimal getBigDecimalValue(InternalWorkingMemory workingMemory,
                                             Object object) {
            // TODO Auto-generated method stub
            return null;
        }

        public BigInteger getBigIntegerValue(InternalWorkingMemory workingMemory,
                                             Object object) {
            // TODO Auto-generated method stub
            return null;
        }

        public BigDecimal getBigDecimalValue(Object object) {
            // TODO Auto-generated method stub
            return null;
        }

        public BigInteger getBigIntegerValue(Object object) {
            // TODO Auto-generated method stub
            return null;
        }
    }
    
}
