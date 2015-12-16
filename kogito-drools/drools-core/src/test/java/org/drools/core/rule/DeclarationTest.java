/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.rule;

import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.base.ClassFieldAccessorStore;
import org.drools.core.base.ClassObjectType;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.test.model.Cheese;
import org.junit.Before;
import org.junit.Test;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

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
                                                                "type" );

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
                    declaration.getDeclarationClass() );

        assertSame( extractor,
                    declaration.getExtractor() );

        assertEquals( 5,
                      declaration.getPattern().getOffset() );

    }

    @Test
    public void testGetFieldValue() throws IntrospectionException {
        final InternalReadAccessor extractor = store.getReader( Cheese.class,
                                                                "type" );

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
