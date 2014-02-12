/*
 * Copyright 2010 JBoss Inc
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

import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.core.base.ClassObjectType;
import org.drools.core.test.model.Cheese;
import org.drools.core.facttemplates.Fact;
import org.drools.core.facttemplates.FactTemplate;
import org.drools.core.facttemplates.FactTemplateImpl;
import org.drools.core.facttemplates.FactTemplateObjectType;
import org.drools.core.facttemplates.FieldTemplate;
import org.drools.core.facttemplates.FieldTemplateImpl;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.spi.ObjectType;

public class PatternTest {

    @Test
    public void testDeclarationsObjectType() throws Exception {
        final ObjectType type = new ClassObjectType( Cheese.class );
        final Pattern col = new Pattern( 0,
                                       type,
                                       "foo" );
        final Declaration dec = col.getDeclaration();
        final InternalReadAccessor ext = dec.getExtractor();
        assertEquals( Cheese.class,
                      ext.getExtractToClass() );

        final Cheese stilton = new Cheese( "stilton",
                                           42 );

        assertEquals( stilton,
                      dec.getValue( null, stilton ) );

    }

    @Test
    public void testDeclarationsFactTemplate() throws Exception {

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

        final ObjectType type = new FactTemplateObjectType( cheese );

        final Pattern col = new Pattern( 0,
                                       type,
                                       "foo" );
        final Declaration dec = col.getDeclaration();
        final InternalReadAccessor ext = dec.getExtractor();
        assertEquals( Fact.class,
                      ext.getExtractToClass() );

        final Fact stilton = cheese.createFact( 10 );
        stilton.setFieldValue( "name",
                               "stilton" );
        stilton.setFieldValue( "price",
                               new Integer( 200 ) );

        assertEquals( stilton,
                      dec.getValue( null, stilton ) );
    }

}
