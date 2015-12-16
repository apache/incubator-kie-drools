/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.base.extractors;

import java.util.Vector;

import org.drools.core.common.ProjectClassLoader;
import org.junit.Before;
import org.junit.Test;
import org.kie.internal.utils.ClassLoaderUtil;

import static org.junit.Assert.*;

import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.base.ClassFieldAccessorStore;
import org.drools.core.test.model.Address;
import org.drools.core.test.model.Person;
import org.drools.core.rule.MVELDialectRuntimeData;

public class MVELClassFieldExtractorTest {

    ClassFieldAccessorStore store = new ClassFieldAccessorStore();
    MVELObjectClassFieldReader    extractor;
    private final Person[]  person   = new Person[2];
    private final Address[] business = new Address[2];
    private final Address[] home     = new Address[2];

    @Before
    public void setUp() throws Exception {
        store.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        store.setEagerWire( true );

        extractor = ( MVELObjectClassFieldReader ) store.getMVELReader(  Person.class.getPackage().getName(),
                                                                         Person.class.getName(),
                                                                         "addresses['home'].street",
                                                                         true,
                                                                         String.class );
        MVELDialectRuntimeData data = new MVELDialectRuntimeData();
        data.addImport( Person.class.getSimpleName(), Person.class );
        data.onAdd( null, ProjectClassLoader.createProjectClassLoader() );
        
        extractor.compile( data );
        
        
        person[0] = new Person( "bob",
                                30 );
        business[0] = new Address( "Business Street",
                                   "999",
                                   null );
        home[0] = new Address( "Home Street",
                               "555",
                               "55555555" );
        person[0].getAddresses().put( "business",
                                      business[0] );
        person[0].getAddresses().put( "home",
                                      home[0] );

        person[1] = new Person( "mark",
                                35 );
        business[1] = new Address( "Another Business Street",
                                   "999",
                                   null );
        home[1] = new Address( "Another Home Street",
                               "555",
                               "55555555" );
        person[1].getAddresses().put( "business",
                                      business[1] );
        person[1].getAddresses().put( "home",
                                      home[1] );

    }

    @Test
    public void testGetBooleanValue() {
        try {
            this.extractor.getBooleanValue( null,
                                            this.person[0] );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    @Test
    public void testGetByteValue() {
        try {
            this.extractor.getByteValue( null,
                                         this.person[0] );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    @Test
    public void testGetCharValue() {
        try {
            this.extractor.getCharValue( null,
                                         this.person[0] );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    @Test
    public void testGetDoubleValue() {
        try {
            this.extractor.getDoubleValue( null,
                                           this.person[0] );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    @Test
    public void testGetFloatValue() {
        try {
            this.extractor.getFloatValue( null,
                                          this.person[0] );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    @Test
    public void testGetIntValue() {
        try {
            this.extractor.getIntValue( null,
                                        this.person[0] );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    @Test
    public void testGetLongValue() {
        try {
            this.extractor.getLongValue( null,
                                         this.person[0] );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    @Test
    public void testGetShortValue() {
        try {
            this.extractor.getShortValue( null,
                                          this.person[0] );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    @Test
    public void testGetValue() {
        try {
            assertEquals( home[0].getStreet(),
                                 this.extractor.getValue( null,
                                                          this.person[0] ) );
            assertTrue( this.extractor.getValue( null,
                                                        this.person[0] ) instanceof String );
        } catch ( final Exception e ) {
            fail( "Should not throw an exception" );
        }
    }

    @Test
    public void testIsNullValue() {
        try {
            assertFalse( this.extractor.isNullValue( null,
                                                     this.person[0] ) );
            
            MVELObjectClassFieldReader nullExtractor =  ( MVELObjectClassFieldReader ) store.getMVELReader(  Person.class.getPackage().getName(),
                                                                                                             Person.class.getName(),
                                                                                                             "addresses['business'].phone",
                                                                                                             true,
                                                                                                             String.class );
            MVELDialectRuntimeData data = new MVELDialectRuntimeData();
            data.addImport( Person.class.getSimpleName(), Person.class );
            data.onAdd( null, ProjectClassLoader.createProjectClassLoader() );
            
            nullExtractor.compile( data );            
//
//            InternalReadAccessor nullExtractor = store.getReader( Person.class,
//                                                                  "addresses['business'].phone",
//                                                                  getClass().getClassLoader() );
            assertTrue( nullExtractor.isNullValue( null,
                                                   this.person[0] ) );
        } catch ( final Exception e ) {
            fail( "Should not throw an exception" );
        }
    }

    @Test
    public void testMultithreads() {
        final int THREAD_COUNT = 30;

        try {
            final Vector errors = new Vector();

            final Thread t[] = new Thread[THREAD_COUNT];
            for ( int j = 0; j < 10; j++ ) {
                for ( int i = 0; i < t.length; i++ ) {
                    final int ID = i;
                    t[i] = new Thread() {
                        public void run() {
                            try {
                                final int ITERATIONS = 300;
                                for ( int k = 0; k < ITERATIONS; k++ ) {
                                    String value = (String) extractor.getValue( null,
                                                                                person[ID % 2] );
                                    if ( !home[ID % 2].getStreet().equals( value ) ) {
                                        errors.add( "THREAD(" + ID + "): Wrong value at iteration " + k + ". Value='" + value + "'\n" );
                                    }
                                }
                            } catch ( Exception ex ) {
                                ex.printStackTrace();
                                errors.add( ex );
                            }
                        }

                    };
                    t[i].start();
                }
                for ( int i = 0; i < t.length; i++ ) {
                    t[i].join();
                }
            }
            if ( !errors.isEmpty() ) {
                fail( " Errors occured during execution\n" + errors.toString()  );
            }
        } catch ( InterruptedException e ) {
            e.printStackTrace();
            fail( "Unexpected exception running test: " + e.getMessage() );
        }
    }

}
