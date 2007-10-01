package org.drools.base.extractors;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.drools.Address;
import org.drools.Person;
import org.drools.base.ClassFieldExtractorCache;
import org.drools.spi.Extractor;

public class MVELClassFieldExtractorTest extends TestCase {

    Extractor extractor = ClassFieldExtractorCache.getInstance().getExtractor( Person.class,
                                                                               "addresses['home'].street",
                                                                               getClass().getClassLoader() );
    Person    person    = null;

    protected void setUp() throws Exception {
        super.setUp();
        person = new Person( "bob",
                             30 );
        Address business = new Address( "Business Street",
                                        "999",
                                        null );
        Address home = new Address( "Home Street",
                                    "555",
                                    "55555555" );
        person.getAddresses().put( "business",
                                   business );
        person.getAddresses().put( "home",
                                   home );
    }

    public void testGetBooleanValue() {
        try {
            this.extractor.getBooleanValue( null,
                                            this.person );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    public void testGetByteValue() {
        try {
            this.extractor.getByteValue( null,
                                         this.person );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    public void testGetCharValue() {
        try {
            this.extractor.getCharValue( null,
                                         this.person );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    public void testGetDoubleValue() {
        try {
            this.extractor.getDoubleValue( null,
                                           this.person );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    public void testGetFloatValue() {
        try {
            this.extractor.getFloatValue( null,
                                          this.person );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    public void testGetIntValue() {
        try {
            this.extractor.getIntValue( null,
                                        this.person );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    public void testGetLongValue() {
        try {
            this.extractor.getLongValue( null,
                                         this.person );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    public void testGetShortValue() {
        try {
            this.extractor.getShortValue( null,
                                          this.person );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    public void testGetValue() {
        try {
            Assert.assertEquals( "Home Street",
                                 this.extractor.getValue( null,
                                                          this.person ) );
            Assert.assertTrue( this.extractor.getValue( null,
                                                        this.person ) instanceof String );
        } catch ( final Exception e ) {
            fail( "Should not throw an exception" );
        }
    }

    public void testIsNullValue() {
        try {
            Assert.assertFalse( this.extractor.isNullValue( null,
                                                            this.person ) );

            Extractor nullExtractor = ClassFieldExtractorCache.getInstance().getExtractor( Person.class,
                                                                                           "addresses['business'].phone",
                                                                                           getClass().getClassLoader() );
            Assert.assertTrue( nullExtractor.isNullValue( null,
                                                          this.person ) );
        } catch ( final Exception e ) {
            fail( "Should not throw an exception" );
        }
    }

}
