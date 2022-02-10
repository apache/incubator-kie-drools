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
import org.drools.core.reteoo.CoreComponentFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;


public class FactTemplateTest {
    @Test
    public void testFieldsAndGetters() {
        InternalKnowledgePackage pkg = CoreComponentFactory.get().createKnowledgePackage( "org.store" );
        final FieldTemplate cheeseName = new FieldTemplateImpl( "name", String.class );
        final FieldTemplate cheesePrice = new FieldTemplateImpl( "price", Integer.class );
        final FieldTemplate[] fields = new FieldTemplate[]{cheeseName, cheesePrice};
        final FactTemplate cheese = new FactTemplateImpl( pkg,
                                                          "Cheese",
                                                          fields );

        assertEquals( "org.store",
                      cheese.getPackage().getName() );
        assertEquals( "Cheese",
                      cheese.getName() );

        assertEquals( 2,
                      cheese.getNumberOfFields() );

        assertSame( cheeseName,
                    cheese.getFieldTemplate( "name" ) );
        assertSame( cheesePrice,
                    cheese.getFieldTemplate( "price" ) );

        assertEquals( 0,
                      cheese.getFieldTemplateIndex( "name" ) );
        assertEquals( 1,
                      cheese.getFieldTemplateIndex( "price" ) );
    }

    @Test
    public void testEqualsAndHashCode() {
        InternalKnowledgePackage pkg = CoreComponentFactory.get().createKnowledgePackage( "org.store" );

        // Create cheese1 with name and price fields
        final FieldTemplate cheeseName = new FieldTemplateImpl( "name", String.class );
        final FieldTemplate cheesePrice = new FieldTemplateImpl( "price", Integer.class );
        final FieldTemplate[] fields1 = new FieldTemplate[]{cheeseName, cheesePrice};
        final FactTemplate cheese1 = new FactTemplateImpl( pkg,
                                                           "Cheese",
                                                           fields1 );

        // Create cheese2 with type and price fields
        final FieldTemplate cheeseType = new FieldTemplateImpl( "type", String.class );
        final FieldTemplate[] fields2 = new FieldTemplate[]{cheeseType, cheesePrice};
        final FactTemplate cheese2 = new FactTemplateImpl( pkg,
                                                           "Cheese",
                                                           fields2 );

        assertNotSame( cheese1,
                       cheese2 );

        assertFalse( cheese1.equals( cheese2 ) );

        assertFalse( cheese1.hashCode() == cheese2.hashCode() );

        // create cheese3 with name and price fields, using new instances
        final FieldTemplate cheeseName2 = new FieldTemplateImpl( "name", String.class );
        final FieldTemplate cheesePrice2 = new FieldTemplateImpl( "price", Integer.class );
        final FieldTemplate[] fields3 = new FieldTemplate[]{cheeseName2, cheesePrice2};
        final FactTemplate cheese3 = new FactTemplateImpl( pkg,
                                                           "Cheese",
                                                           fields3 );

        assertNotSame( cheese1,
                       cheese3 );
        assertEquals( cheese1,
                      cheese3 );
        assertEquals( cheese1.hashCode(),
                      cheese3.hashCode() );
    }

    @Test
    public void testFacts() {
        InternalKnowledgePackage pkg = CoreComponentFactory.get().createKnowledgePackage( "org.store" );
        final FieldTemplate cheeseName = new FieldTemplateImpl( "name", String.class );
        final FieldTemplate cheesePrice = new FieldTemplateImpl( "price", Integer.class );
        final FieldTemplate[] fields1 = new FieldTemplate[]{cheeseName, cheesePrice};
        final FactTemplate cheese1 = new FactTemplateImpl( pkg,
                                                           "Cheese",
                                                           fields1 );

        final Fact stilton1 = cheese1.createFact();
        stilton1.set( "name", "stilton" );
        stilton1.set( "price", 200 );

        final Fact stilton2 = cheese1.createFact();
        stilton2.set( "name", "stilton" );
        stilton2.set( "price",  200 );

        assertEquals( stilton1,
                      stilton2 );
        assertEquals( stilton1.hashCode(),
                      stilton2.hashCode() );

        final Fact brie = cheese1.createFact();
        brie.set( "name", "brie" );
        brie.set( "price", 55 );

        assertFalse( stilton1.equals( brie ) );
        assertFalse( stilton1.hashCode() == brie.hashCode() );

    }
}
