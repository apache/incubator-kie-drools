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
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;
import org.drools.core.spi.InternalReadAccessor;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

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

        assertThat(extractName.getValue(null, stilton)).isEqualTo("stilton");

        assertThat(extractPrice.getValue(null, stilton)).isEqualTo(200);

        assertThat(extractName.isNullValue(null, stilton)).isFalse();
        
        stilton.setFieldValue( "name",
                               null );
        

        assertThat(extractName.isNullValue(null, stilton)).isTrue();
        assertThat(extractPrice.isNullValue(null, stilton)).isFalse();
        
        final Fact brie = cheese.createFact( 12 );
        brie.setFieldValue( "name",
                            "brie" );
        brie.setFieldValue( "price",
                            new Integer( 55 ) );

        assertThat(extractName.getValue(null, brie)).isEqualTo("brie");

        assertThat(extractPrice.getValue(null, brie)).isEqualTo(55);

        assertThat(extractName.isNullValue(null, brie)).isFalse();
        
        brie.setFieldValue( "name", null );

        assertThat(extractName.isNullValue(null, brie)).isTrue();
        assertThat(extractPrice.isNullValue(null, stilton)).isFalse();
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
        assertThat(declaration.getValue(null, brie)).isEqualTo("brie");
    }
}
