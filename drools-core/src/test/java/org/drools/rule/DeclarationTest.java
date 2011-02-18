/*
 * Copyright 2005 JBoss Inc
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

package org.drools.rule;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.Cheese;
import org.drools.base.ClassFieldAccessorCache;
import org.drools.base.ClassFieldAccessorStore;
import org.drools.base.ClassObjectType;
import org.drools.spi.InternalReadAccessor;

public class DeclarationTest {

    ClassFieldAccessorStore store = new ClassFieldAccessorStore();

    @Before
    public void setUp() throws Exception {
        store.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        store.setEagerWire( true );
    }

    @Test
    public void testDeclaration() throws IntrospectionException {
        final InternalReadAccessor extractor = store.getReader( Cheese.class,
                                                                "type",
                                                                getClass().getClassLoader() );

        final Pattern pattern = new Pattern( 5,
                                             new ClassObjectType( Cheese.class ) );

        // Bind the extractor to a decleration
        // Declarations know the pattern they derive their value from
        final Declaration declaration = new Declaration( "typeOfCheese",
                                                         extractor,
                                                         pattern );

        assertEquals( "typeOfCheese",
                      declaration.getIdentifier() );

        assertSame( String.class,
                    declaration.getExtractor().getExtractToClass() );

        assertSame( extractor,
                    declaration.getExtractor() );

        assertEquals( 5,
                      declaration.getPattern().getOffset() );

    }

    @Test
    public void testGetFieldValue() throws IntrospectionException {
        final InternalReadAccessor extractor = store.getReader( Cheese.class,
                                                                "type",
                                                                getClass().getClassLoader() );

        final Pattern pattern = new Pattern( 5,
                                             new ClassObjectType( Cheese.class ) );

        // Bind the extractor to a decleration 
        // Declarations know the pattern they derive their value from 
        final Declaration declaration = new Declaration( "typeOfCheese",
                                                         extractor,
                                                         pattern );

        // Create some facts
        final Cheese cheddar = new Cheese( "cheddar",
                                           5 );

        // Check we can extract Declarations correctly
        assertEquals( "cheddar",
                      declaration.getValue( null,
                                            cheddar ) );
    }

    public static int getIndex(final Class clazz,
                               final String name) throws IntrospectionException {
        final PropertyDescriptor[] descriptors = Introspector.getBeanInfo( clazz ).getPropertyDescriptors();
        for ( int i = 0; i < descriptors.length; i++ ) {
            if ( descriptors[i].getName().equals( name ) ) {
                return i;
            }
        }
        return -1;
    }
}
