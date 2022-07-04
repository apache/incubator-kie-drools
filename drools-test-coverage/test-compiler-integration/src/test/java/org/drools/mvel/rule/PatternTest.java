/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mvel.rule;

import org.drools.core.base.ClassObjectType;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.facttemplates.Fact;
import org.drools.core.facttemplates.FactTemplate;
import org.drools.core.facttemplates.FactTemplateImpl;
import org.drools.core.facttemplates.FactTemplateObjectType;
import org.drools.core.facttemplates.FieldTemplate;
import org.drools.core.facttemplates.FieldTemplateImpl;
import org.drools.core.reteoo.CoreComponentFactory;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.accessor.ReadAccessor;
import org.drools.core.base.ObjectType;
import org.drools.core.test.model.Cheese;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PatternTest {

    @Test
    public void testDeclarationsObjectType() throws Exception {
        final ObjectType type = new ClassObjectType( Cheese.class );
        final Pattern col = new Pattern( 0,
                                       type,
                                       "foo" );
        final Declaration dec = col.getDeclaration();
        final ReadAccessor ext = dec.getExtractor();
        assertThat(ext.getExtractToClass()).isEqualTo(Cheese.class);

        final Cheese stilton = new Cheese( "stilton",
                                           42 );

        assertThat(dec.getValue(null, stilton)).isEqualTo(stilton);

    }

    @Test
    public void testDeclarationsFactTemplate() throws Exception {

        InternalKnowledgePackage pkg = CoreComponentFactory.get().createKnowledgePackage( "org.store" );
        final FieldTemplate cheeseName = new FieldTemplateImpl( "name", String.class );
        final FieldTemplate cheesePrice = new FieldTemplateImpl( "price", Integer.class );
        final FieldTemplate[] fields = new FieldTemplate[]{cheeseName, cheesePrice};
        final FactTemplate cheese = new FactTemplateImpl( pkg, "Cheese", fields );

        final ObjectType type = new FactTemplateObjectType( cheese );

        final Pattern col = new Pattern( 0,
                                       type,
                                       "foo" );
        final Declaration dec = col.getDeclaration();
        final ReadAccessor ext = dec.getExtractor();
        assertThat(ext.getExtractToClass()).isEqualTo(Fact.class);

        final Fact stilton = cheese.createFact();
        stilton.set( "name",
                               "stilton" );
        stilton.set( "price",
                               new Integer( 200 ) );

        assertThat(dec.getValue(null, stilton)).isEqualTo(stilton);
    }

}
