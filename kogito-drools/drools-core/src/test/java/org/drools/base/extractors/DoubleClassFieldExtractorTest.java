package org.drools.base.extractors;

import junit.framework.Assert;

import org.drools.base.ClassFieldExtractorCache;
import org.drools.base.ClassFieldExtractorFactory;
import org.drools.base.TestBean;
import org.drools.spi.Extractor;

public class DoubleClassFieldExtractorTest extends BaseClassFieldExtractorsTest {
    private static final double VALUE     = 7;

    Extractor                   extractor = ClassFieldExtractorCache.getExtractor( TestBean.class,
                                                                                   "doubleAttr" );
    TestBean                    bean      = new TestBean();

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testGetBooleanValue() {
        try {
            this.extractor.getBooleanValue( this.bean );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    public void testGetByteValue() {
        try {
            Assert.assertEquals( (byte) DoubleClassFieldExtractorTest.VALUE,
                                 this.extractor.getByteValue( this.bean ) );
        } catch ( final Exception e ) {
            fail( "Should not throw an exception" );
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
            Assert.assertEquals( DoubleClassFieldExtractorTest.VALUE,
                                 this.extractor.getDoubleValue( this.bean ),
                                 0.01 );
        } catch ( final Exception e ) {
            fail( "Should not throw an exception" );
        }
    }

    public void testGetFloatValue() {
        try {
            Assert.assertEquals( DoubleClassFieldExtractorTest.VALUE,
                                 this.extractor.getFloatValue( this.bean ),
                                 0.01 );
        } catch ( final Exception e ) {
            fail( "Should not throw an exception" );
        }
    }

    public void testGetIntValue() {
        try {
            Assert.assertEquals( (int) DoubleClassFieldExtractorTest.VALUE,
                                 this.extractor.getIntValue( this.bean ) );
        } catch ( final Exception e ) {
            fail( "Should not throw an exception" );
        }
    }

    public void testGetLongValue() {
        try {
            Assert.assertEquals( (long) DoubleClassFieldExtractorTest.VALUE,
                                 this.extractor.getLongValue( this.bean ) );
        } catch ( final Exception e ) {
            fail( "Should not throw an exception" );
        }
    }

    public void testGetShortValue() {
        try {
            Assert.assertEquals( (short) DoubleClassFieldExtractorTest.VALUE,
                                 this.extractor.getShortValue( this.bean ) );
        } catch ( final Exception e ) {
            fail( "Should not throw an exception" );
        }
    }

    public void testGetValue() {
        try {
            Assert.assertEquals( new Double( DoubleClassFieldExtractorTest.VALUE ),
                                 this.extractor.getValue( this.bean ) );
            Assert.assertTrue( this.extractor.getValue( this.bean ) instanceof Double );
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
