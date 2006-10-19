package org.drools.base.extractors;

import junit.framework.Assert;

import org.drools.base.ClassFieldExtractorFactory;
import org.drools.base.TestBean;
import org.drools.spi.Extractor;

public class CharClassFieldExtractorTest extends ClassFieldExtractorsBaseTest {
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
            this.extractor.getByteValue( this.bean );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
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
            Assert.assertEquals( new Character( 'a' ),
                                 this.extractor.getValue( this.bean ) );
            Assert.assertTrue( this.extractor.getValue( this.bean ) instanceof Character );
        } catch ( final Exception e ) {
            fail( "Should not throw an exception" );
        }
    }
}
