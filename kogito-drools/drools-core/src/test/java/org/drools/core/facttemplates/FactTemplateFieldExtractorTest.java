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

package org.drools.core.facttemplates;

import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;
import org.drools.core.spi.InternalReadAccessor;

public class FactTemplateFieldExtractorTest {
    @Test
    public void testExtractor() {
        InternalKnowledgePackage pkg = new KnowledgePackageImpl( "org.store" );

        final FieldTemplate cheeseName = new FieldTemplateImpl( "name",
                                                                0,
                                                                String.class );
        final FieldTemplate cheesePrice = new FieldTemplateImpl( "price",
                                                                 1,
                                                                 Integer.class );
        final FieldTemplate[] fields = new FieldTemplate[]{cheeseName, cheesePrice};
        final FactTemplate cheese = new FactTemplateImpl( pkg,
                                                          "Cheese",
                                                          fields );

        final InternalReadAccessor extractName = new FactTemplateFieldExtractor( cheese,
                                                                      0 );
        final InternalReadAccessor extractPrice = new FactTemplateFieldExtractor( cheese,
                                                                       1 );

        final Fact stilton = cheese.createFact( 10 );
        stilton.setFieldValue( "name",
                               "stilton" );
        stilton.setFieldValue( "price",
                               new Integer( 200 ) );

        assertEquals( "stilton",
                      extractName.getValue( null, stilton ) );

        assertEquals( new Integer( 200 ),
                      extractPrice.getValue( null, stilton ) );

        assertFalse( extractName.isNullValue( null, stilton ) );
        
        stilton.setFieldValue( "name",
                               null );
        
        assertTrue( extractName.isNullValue( null, stilton ) );
        assertFalse( extractPrice.isNullValue( null, stilton ) );
        
        final Fact brie = cheese.createFact( 12 );
        brie.setFieldValue( "name",
                            "brie" );
        brie.setFieldValue( "price",
                            new Integer( 55 ) );

        assertEquals( "brie",
                      extractName.getValue( null, brie ) );

        assertEquals( new Integer( 55 ),
                      extractPrice.getValue( null, brie ) );
        
        assertFalse( extractName.isNullValue( null, brie ) );
        
        brie.setFieldValue( "name",
                            null );
        
        assertTrue( extractName.isNullValue( null, brie ) );
        assertFalse( extractPrice.isNullValue( null, stilton ) );
    }

    @Test
    public void testDeclaration() {
        InternalKnowledgePackage pkg = new KnowledgePackageImpl( "org.store" );

        final FieldTemplate cheeseName = new FieldTemplateImpl( "name",
                                                                0,
                                                                String.class );
        final FieldTemplate cheesePrice = new FieldTemplateImpl( "price",
                                                                 1,
                                                                 Integer.class );
        final FieldTemplate[] fields = new FieldTemplate[]{cheeseName, cheesePrice};
        final FactTemplate cheese = new FactTemplateImpl( pkg,
                                                          "Cheese",
                                                          fields );

        final InternalReadAccessor extractName = new FactTemplateFieldExtractor( cheese,
                                                                      0 );

        final Pattern pattern = new Pattern( 0,
                                          new FactTemplateObjectType( cheese ) );

        final Declaration declaration = new Declaration( "typeOfCheese",
                                                         extractName,
                                                         pattern );

        final Fact brie = cheese.createFact( 12 );
        brie.setFieldValue( "name",
                            "brie" );
        brie.setFieldValue( "price",
                            new Integer( 55 ) );

        // Check we can extract Declarations correctly 
        assertEquals( "brie",
                      declaration.getValue( null, brie ) );
    }
}
