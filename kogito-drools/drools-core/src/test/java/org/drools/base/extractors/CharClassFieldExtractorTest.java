package org.drools.base.extractors;

import junit.framework.Assert;

import org.drools.base.ClassFieldExtractorFactory;
import org.drools.base.TestBean;
import org.drools.spi.Extractor;

public class CharClassFieldExtractorTest extends BaseClassFieldExtractorsTest {
    Extractor extractor = ClassFieldExtractorFactory.getClassFieldExtractor( TestBean.class,
                                                                             "charAttr" );
    TestBean  bean      = new TestBean();

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
            Assert.assertEquals( 'a',
                                 this.extractor.getByteValue( this.bean ) );
        } catch ( final Exception e ) {
            fail( "Should not throw exception" );
        }
    }

    public void testGetCharValue() {
        try {
            Assert.assertEquals( 'a',
                                 this.extractor.getCharValue( this.bean ) );
        } catch ( final Exception e ) {
            fail( "Should not throw exception" );
        }
    }

    public void testGetDoubleValue() {
        try {
            Assert.assertEquals( 'a',
                                 (int) this.extractor.getDoubleValue( this.bean ) );
        } catch ( final Exception e ) {
            fail( "Should not throw exception" );
        }
    }

    public void testGetFloatValue() {
        try {
            Assert.assertEquals( 'a',
                                 (int) this.extractor.getFloatValue( this.bean ) );
        } catch ( final Exception e ) {
            fail( "Should not throw exception" );
        }
    }

    public void testGetIntValue() {
        try {
            Assert.assertEquals( 'a',
                                 this.extractor.getIntValue( this.bean ) );
        } catch ( final Exception e ) {
            fail( "Should not throw exception" );
        }
    }

    public void testGetLongValue() {
        try {
            Assert.assertEquals( 'a',
                                 (int) this.extractor.getLongValue( this.bean ) );
        } catch ( final Exception e ) {
            fail( "Should not throw exception" );
        }
    }

    public void testGetShortValue() {
        try {
            Assert.assertEquals( 'a',
                                 this.extractor.getShortValue( this.bean ) );
        } catch ( final Exception e ) {
            fail( "Should not throw exception" );
        }
    }

    public void testGetValue() {
        try {
            Assert.assertEquals( 'a',
                                 ((Number) this.extractor.getValue( this.bean )).intValue() );
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
