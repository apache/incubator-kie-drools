package org.drools.base.extractors;

import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.drools.base.ClassFieldExtractorFactory;
import org.drools.base.TestBean;
import org.drools.spi.Extractor;

public class ObjectClassFieldExtractorTest extends ClassFieldExtractorsBaseTest {
    Extractor extractor = ClassFieldExtractorFactory.getClassFieldExtractor( TestBean.class, "listAttr" );
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
            Assert.assertEquals( Collections.EMPTY_LIST, this.extractor.getValue( bean ) );
            Assert.assertTrue( this.extractor.getValue( bean ) instanceof List );
        } catch (Exception e) {
            fail("Should not throw an exception");
        }
    }
}
