package org.drools.base.extractors;

import junit.framework.Assert;

import org.drools.base.ClassFieldExtractorFactory;
import org.drools.base.TestBean;
import org.drools.spi.Extractor;

public class BooleanClassFieldExtractorTest extends ClassFieldExtractorsBaseTest {
    Extractor extractor = ClassFieldExtractorFactory.getClassFieldExtractor( TestBean.class, "booleanAttr" );
    TestBean bean = new TestBean();
    
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testGetBooleanValue() {
        try {
            Assert.assertTrue( this.extractor.getBooleanValue( bean ) );
        } catch (Exception e) {
            fail("Should not throw exception");
        }
    }

    public void testGetByteValue() {
        try {
            this.extractor.getByteValue( bean );
            fail("Should have throw an exception");
        } catch (Exception e) {
            // success
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
            this.extractor.getDoubleValue( bean );
            fail("Should have throw an exception");
        } catch (Exception e) {
            // success
        }
    }

    public void testGetFloatValue() {
        try {
            this.extractor.getFloatValue( bean );
            fail("Should have throw an exception");
        } catch (Exception e) {
            // success
        }
    }

    public void testGetIntValue() {
        try {
            this.extractor.getIntValue( bean );
            fail("Should have throw an exception");
        } catch (Exception e) {
            // success
        }
    }

    public void testGetLongValue() {
        try {
            this.extractor.getLongValue( bean );
            fail("Should have throw an exception");
        } catch (Exception e) {
            // success
        }
    }

    public void testGetShortValue() {
        try {
            this.extractor.getShortValue( bean );
            fail("Should have throw an exception");
        } catch (Exception e) {
            // success
        }
    }

    public void testGetValue() {
        try {
            Assert.assertSame( Boolean.TRUE, this.extractor.getValue( bean ) );
        } catch (Exception e) {
            fail("Should not throw an exception");
        }
    }

}
