package org.drools.rule;

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

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.base.ClassFieldExtractor;
import org.drools.base.ClassObjectType;
import org.drools.spi.FieldExtractor;

public class DeclarationTest extends TestCase {

    public void testDeclaration() throws IntrospectionException {
        final FieldExtractor extractor = new ClassFieldExtractor( Cheese.class,
                                                                  "type" );

        /* Bind the extractor to a decleration */
        /* Declarations know the column they derive their value from */
        final Declaration declaration = new Declaration( "typeOfCheese",
                                                         extractor,
                                                         5 );

        assertEquals( "typeOfCheese",
                      declaration.getIdentifier() );

        assertSame( String.class,
                    ((ClassObjectType) declaration.getObjectType()).getClassType() );

        assertSame( extractor,
                    declaration.getExtractor() );

        assertEquals( 5,
                      declaration.getColumn() );

    }

    public void testGetFieldValue() throws IntrospectionException {
        final FieldExtractor extractor = new ClassFieldExtractor( Cheese.class,
                                                                  "type" );

        /* Bind the extractor to a decleration */
        /* Declarations know the column they derive their value from */
        final Declaration declaration = new Declaration( "typeOfCheese",
                                                         extractor,
                                                         5 );

        /* Create some facts */
        final Cheese cheddar = new Cheese( "cheddar",
                                           5 );

        /* Check we can extract Declarations correctly */
        assertEquals( "cheddar",
                      declaration.getValue( cheddar ) );
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