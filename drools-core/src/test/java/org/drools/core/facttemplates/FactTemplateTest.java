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

import static org.assertj.core.api.Assertions.assertThat;

public class FactTemplateTest {
    @Test
    public void testFieldsAndGetters() {
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

        assertThat(cheese.getPackage().getName()).isEqualTo("org.store");
        assertThat(cheese.getName()).isEqualTo("Cheese");

        assertThat(cheese.getNumberOfFields()).isEqualTo(2);

        assertThat(cheese.getFieldTemplate("name")).isSameAs(cheeseName);
        assertThat(cheese.getFieldTemplate("price")).isSameAs(cheesePrice);

        assertThat(cheese.getFieldTemplateIndex("name")).isEqualTo(0);
        assertThat(cheese.getFieldTemplateIndex("price")).isEqualTo(1);
    }

    @Test
    public void testEqualsAndHashCode() {
        InternalKnowledgePackage pkg = new KnowledgePackageImpl( "org.store" );

        // Create cheese1 with name and price fields
        final FieldTemplate cheeseName = new FieldTemplateImpl( "name",
                                                                0,
                                                                String.class );
        final FieldTemplate cheesePrice = new FieldTemplateImpl( "price",
                                                                 1,
                                                                 Integer.class );
        final FieldTemplate[] fields1 = new FieldTemplate[]{cheeseName, cheesePrice};
        final FactTemplate cheese1 = new FactTemplateImpl( pkg,
                                                           "Cheese",
                                                           fields1 );

        // Create cheese2 with type and price fields
        final FieldTemplate cheeseType = new FieldTemplateImpl( "type",
                                                                0,
                                                                String.class );
        final FieldTemplate[] fields2 = new FieldTemplate[]{cheeseType, cheesePrice};
        final FactTemplate cheese2 = new FactTemplateImpl( pkg,
                                                           "Cheese",
                                                           fields2 );

        assertThat(cheese1).isNotSameAs(cheese2);
        assertThat(cheese1).isNotEqualTo(cheese2);
        assertThat(cheese1.hashCode()).isNotEqualTo(cheese2.hashCode());

        // create cheese3 with name and price fields, using new instances
        final FieldTemplate cheeseName2 = new FieldTemplateImpl( "name",
                                                                 0,
                                                                 String.class );
        final FieldTemplate cheesePrice2 = new FieldTemplateImpl( "price",
                                                                  1,
                                                                  Integer.class );
        final FieldTemplate[] fields3 = new FieldTemplate[]{cheeseName2, cheesePrice2};
        final FactTemplate cheese3 = new FactTemplateImpl( pkg,
                                                           "Cheese",
                                                           fields3 );


        assertThat(cheese1).isNotSameAs(cheese3);
        assertThat(cheese3).isEqualTo(cheese1);
        assertThat(cheese3.hashCode()).isEqualTo(cheese1.hashCode());
    }

    @Test
    public void testFacts() {
        InternalKnowledgePackage pkg = new KnowledgePackageImpl( "org.store" );
        final FieldTemplate cheeseName = new FieldTemplateImpl( "name",
                                                                0,
                                                                String.class );
        final FieldTemplate cheesePrice = new FieldTemplateImpl( "price",
                                                                 1,
                                                                 Integer.class );
        final FieldTemplate[] fields1 = new FieldTemplate[]{cheeseName, cheesePrice};
        final FactTemplate cheese1 = new FactTemplateImpl( pkg,
                                                           "Cheese",
                                                           fields1 );

        final Fact stilton1 = cheese1.createFact( 10 );
        stilton1.setFieldValue( "name",
                                "stilton" );
        stilton1.setFieldValue( "price",
                                new Integer( 200 ) );

        final Fact stilton2 = cheese1.createFact( 11 );
        stilton2.setFieldValue( 0,
                                "stilton" );
        stilton2.setFieldValue( 1,
                                new Integer( 200 ) );

        assertThat(stilton2).isEqualTo(stilton1);
        assertThat(stilton2.hashCode()).isEqualTo(stilton1.hashCode());

        final Fact brie = cheese1.createFact( 12 );
        brie.setFieldValue( "name",
                            "brie" );
        brie.setFieldValue( "price",
                            new Integer( 55 ) );

        assertThat(stilton1).isNotEqualTo(brie);
        assertThat(stilton1.hashCode()).isNotEqualTo(brie.hashCode());

    }
}
