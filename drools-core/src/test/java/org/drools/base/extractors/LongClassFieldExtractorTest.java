package org.drools.base.extractors;

import junit.framework.Assert;

import org.drools.base.ClassFieldExtractorCache;
import org.drools.base.TestBean;
import org.drools.spi.Extractor;

public class LongClassFieldExtractorTest extends BaseClassFieldExtractorsTest {
    private static final long VALUE     = 5;

    Extractor                 extractor = ClassFieldExtractorCache.getInstance().getExtractor( TestBean.class,
                                                                                               "longAttr",
                                                                                               getClass().getClassLoader() );
    TestBean                  bean      = new TestBean();

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testGetBooleanValue() {
        try {
            this.extractor.getBooleanValue( null,
                                            this.bean );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    public void testGetByteValue() {
        try {
            Assert.assertEquals( LongClassFieldExtractorTest.VALUE,
                                 this.extractor.getByteValue( null,
                                                              this.bean ) );
        } catch ( final Exception e ) {
            fail( "Should not throw an exception" );
        }
    }

    public void testGetCharValue() {
        try {
            this.extractor.getCharValue( null,
                                         this.bean );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    public void testGetDoubleValue() {
        try {
            Assert.assertEquals( LongClassFieldExtractorTest.VALUE,
                                 this.extractor.getDoubleValue( null,
                                                                this.bean ),
                                 0.01 );
        } catch ( final Exception e ) {
            fail( "Should not throw an exception" );
        }
    }

    public void testGetFloatValue() {
        try {
            Assert.assertEquals( LongClassFieldExtractorTest.VALUE,
                                 this.extractor.getFloatValue( null,
                                                               this.bean ),
                                 0.01 );
        } catch ( final Exception e ) {
            fail( "Should not throw an exception" );
        }
    }

    public void testGetIntValue() {
        try {
            Assert.assertEquals( LongClassFieldExtractorTest.VALUE,
                                 this.extractor.getIntValue( null,
                                                             this.bean ) );
        } catch ( final Exception e ) {
            fail( "Should not throw an exception" );
        }
    }

    public void testGetLongValue() {
        try {
            Assert.assertEquals( LongClassFieldExtractorTest.VALUE,
                                 this.extractor.getLongValue( null,
                                                              this.bean ) );
        } catch ( final Exception e ) {
            fail( "Should not throw an exception" );
        }
    }

    public void testGetShortValue() {
        try {
            Assert.assertEquals( LongClassFieldExtractorTest.VALUE,
                                 this.extractor.getShortValue( null,
                                                               this.bean ) );
        } catch ( final Exception e ) {
            fail( "Should not throw an exception" );
        }
    }

    public void testGetValue() {
        try {
            Assert.assertEquals( new Long( (short) LongClassFieldExtractorTest.VALUE ),
                                 this.extractor.getValue( null,
                                                          this.bean ) );
            Assert.assertTrue( this.extractor.getValue( null,
                                                        this.bean ) instanceof Long );
        } catch ( final Exception e ) {
            fail( "Should not throw an exception" );
        }
    }

    public void testIsNullValue() {
        try {
            Assert.assertFalse( this.extractor.isNullValue( null,
                                                            this.bean ) );
        } catch ( final Exception e ) {
            fail( "Should not throw an exception" );
        }
    }
}
