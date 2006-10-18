package org.drools.base.extractors;

import junit.framework.Assert;

import org.drools.base.ClassFieldExtractorFactory;
import org.drools.base.TestBean;
import org.drools.spi.Extractor;

public class DoubleClassFieldExtractorTest extends ClassFieldExtractorsBaseTest {
    private static final double VALUE = 7;
    
    Extractor extractor = ClassFieldExtractorFactory.getClassFieldExtractor( TestBean.class, "doubleAttr" );
    TestBean bean = new TestBean();
    
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testGetBooleanValue() {
        try {
            this.extractor.getBooleanValue( bean );
            fail("Should have throw an exception");
        } catch (Exception e) {
            // success
        }
    }

    public void testGetByteValue() {
        try {
            Assert.assertEquals( (byte) VALUE, this.extractor.getByteValue( bean ));
        } catch (Exception e) {
            fail("Should not throw an exception");
        }
    }

    public void testGetCharValue() {
        try {
            this.extractor.getCharValue( bean );
            fail("Should have throw an exception");
        } catch (Exception e) {
            // success
        }
    }

    public void testGetDoubleValue() {
        try {
            Assert.assertEquals( VALUE, this.extractor.getDoubleValue( bean ), 0.01);
        } catch (Exception e) {
            fail("Should not throw an exception");
        }
    }

    public void testGetFloatValue() {
        try {
            Assert.assertEquals( VALUE, this.extractor.getFloatValue( bean ), 0.01);
        } catch (Exception e) {
            fail("Should not throw an exception");
        }
    }

    public void testGetIntValue() {
        try {
            Assert.assertEquals( (int) VALUE, this.extractor.getIntValue( bean ));
        } catch (Exception e) {
            fail("Should not throw an exception");
        }
    }

    public void testGetLongValue() {
        try {
            Assert.assertEquals( (long) VALUE, this.extractor.getLongValue( bean ));
        } catch (Exception e) {
            fail("Should not throw an exception");
        }
    }

    public void testGetShortValue() {
        try {
            Assert.assertEquals( (short) VALUE, this.extractor.getShortValue( bean ));
        } catch (Exception e) {
            fail("Should not throw an exception");
        }
    }

    public void testGetValue() {
        try {
            Assert.assertEquals( new Double((double)VALUE), this.extractor.getValue( bean ) );
            Assert.assertTrue( this.extractor.getValue( bean ) instanceof Double );
        } catch (Exception e) {
            fail("Should not throw an exception");
        }
    }
}
