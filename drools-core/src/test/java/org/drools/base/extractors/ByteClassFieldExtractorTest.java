package org.drools.base.extractors;

import junit.framework.Assert;

import org.drools.base.ClassFieldExtractorFactory;
import org.drools.base.TestBean;
import org.drools.spi.Extractor;

public class ByteClassFieldExtractorTest extends ClassFieldExtractorsBaseTest {
    Extractor extractor = ClassFieldExtractorFactory.getClassFieldExtractor( TestBean.class, "byteAttr" );
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
            Assert.assertEquals( 1, this.extractor.getByteValue( bean ));
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
            Assert.assertEquals( 1.0, this.extractor.getDoubleValue( bean ), 0.01);
        } catch (Exception e) {
            fail("Should not throw an exception");
        }
    }

    public void testGetFloatValue() {
        try {
            Assert.assertEquals( 1.0f, this.extractor.getFloatValue( bean ), 0.01);
        } catch (Exception e) {
            fail("Should not throw an exception");
        }
    }

    public void testGetIntValue() {
        try {
            Assert.assertEquals( 1, this.extractor.getIntValue( bean ));
        } catch (Exception e) {
            fail("Should not throw an exception");
        }
    }

    public void testGetLongValue() {
        try {
            Assert.assertEquals( 1, this.extractor.getLongValue( bean ));
        } catch (Exception e) {
            fail("Should not throw an exception");
        }
    }

    public void testGetShortValue() {
        try {
            Assert.assertEquals( 1, this.extractor.getShortValue( bean ));
        } catch (Exception e) {
            fail("Should not throw an exception");
        }
    }

    public void testGetValue() {
        try {
            Assert.assertEquals( new Byte((byte)1), this.extractor.getValue( bean ) );
            Assert.assertTrue( this.extractor.getValue( bean ) instanceof Byte );
        } catch (Exception e) {
            fail("Should not throw an exception");
        }
    }

}
