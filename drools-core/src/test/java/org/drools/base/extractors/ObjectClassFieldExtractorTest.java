package org.drools.base.extractors;

import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.drools.base.ClassFieldAccessorCache;
import org.drools.base.TestBean;
import org.drools.spi.InternalReadAccessor;

public class ObjectClassFieldExtractorTest extends BaseClassFieldExtractorsTest {

    InternalReadAccessor extractor = ClassFieldAccessorCache.getInstance().getReader( TestBean.class,
                                                                               "listAttr",
                                                                               getClass().getClassLoader() );
    TestBean  bean      = new TestBean();

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
            this.extractor.getByteValue( null,
                                         this.bean );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
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
            this.extractor.getDoubleValue( null,
                                           this.bean );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    public void testGetFloatValue() {
        try {
            this.extractor.getFloatValue( null,
                                          this.bean );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    public void testGetIntValue() {
        try {
            this.extractor.getIntValue( null,
                                        this.bean );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    public void testGetLongValue() {
        try {
            this.extractor.getLongValue( null,
                                         this.bean );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    public void testGetShortValue() {
        try {
            this.extractor.getShortValue( null,
                                          this.bean );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    public void testGetValue() {
        try {
            Assert.assertEquals( Collections.EMPTY_LIST,
                                 this.extractor.getValue( null,
                                                          this.bean ) );
            Assert.assertTrue( this.extractor.getValue( null,
                                                        this.bean ) instanceof List );
        } catch ( final Exception e ) {
            fail( "Should not throw an exception" );
        }
    }

    public void testIsNullValue() {
        try {
            Assert.assertFalse( this.extractor.isNullValue( null,
                                                            this.bean ) );

            InternalReadAccessor nullExtractor = ClassFieldAccessorCache.getInstance().getReader( TestBean.class,
                                                                                           "nullAttr",
                                                                                           getClass().getClassLoader() );
            Assert.assertTrue( nullExtractor.isNullValue( null,
                                                          this.bean ) );
        } catch ( final Exception e ) {
            fail( "Should not throw an exception" );
        }
    }
}
