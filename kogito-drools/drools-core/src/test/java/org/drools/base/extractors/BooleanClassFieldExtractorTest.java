package org.drools.base.extractors;

import junit.framework.Assert;

import org.drools.base.ClassFieldExtractorFactory;
import org.drools.base.TestBean;
import org.drools.spi.Extractor;

public class BooleanClassFieldExtractorTest extends BaseClassFieldExtractorsTest {
    Extractor extractor = ClassFieldExtractorFactory.getClassFieldExtractor( TestBean.class,
                                                                             "booleanAttr" );
    TestBean  bean      = new TestBean();

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testGetBooleanValue() {
        try {
            Assert.assertTrue( this.extractor.getBooleanValue( this.bean ) );
        } catch ( final Exception e ) {
            fail( "Should not throw exception" );
        }
    }

    public void testGetByteValue() {
        try {
            this.extractor.getByteValue( this.bean );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    public void testGetCharValue() {
        try {
            this.extractor.getCharValue( this.bean );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    public void testGetDoubleValue() {
        try {
            this.extractor.getDoubleValue( this.bean );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    public void testGetFloatValue() {
        try {
            this.extractor.getFloatValue( this.bean );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    public void testGetIntValue() {
        try {
            this.extractor.getIntValue( this.bean );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    public void testGetLongValue() {
        try {
            this.extractor.getLongValue( this.bean );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    public void testGetShortValue() {
        try {
            this.extractor.getShortValue( this.bean );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    public void testGetValue() {
        try {
            Assert.assertSame( Boolean.TRUE,
                               this.extractor.getValue( this.bean ) );
        } catch ( final Exception e ) {
            fail( "Should not throw an exception" );
        }
    }

    public void testIsNullValue() {
        try {
            Assert.assertFalse( this.extractor.isNullValue( this.bean ) );
        } catch ( final Exception e ) {
            fail( "Should not throw an exception" );
        }
    }
}
