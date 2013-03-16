package org.drools.compiler.factmodel.traits;

/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.fail;

import java.util.Date;

import org.drools.core.common.AbstractRuleBase;
import org.drools.factmodel.traits.Thing;
import org.drools.factmodel.traits.TraitFactory;
import org.drools.factmodel.traits.TraitProxy;
import org.drools.factmodel.traits.TraitableBean;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.definition.type.FactType;
import org.kie.io.Resource;
import org.kie.io.ResourceFactory;
import org.kie.io.ResourceType;

public class TripleBasedTraitsTest {


    static long t0;

    @BeforeClass
    public static void init() {
        t0 = new Date().getTime();
    }

    @AfterClass
    public static void finish() {
        System.out.println("TIME : "+  (new Date().getTime()-t0));
    }


    @Test
    public void testHasTypes() {

        String source = "org/drools/compiler/factmodel/traits/testTraitDon.drl";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Resource res = ResourceFactory.newClassPathResource(source);
        assertNotNull(res);
        kbuilder.add(res, ResourceType.DRL);
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kb = KnowledgeBaseFactory.newKnowledgeBase();
        kb.addKnowledgePackages(kbuilder.getKnowledgePackages());
        TraitFactory traitBuilder = ((AbstractRuleBase) ((KnowledgeBaseImpl) kb).getRuleBase()).getConfiguration().getComponentFactory().getTraitFactory();
        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.TRIPLES, kb );

        try {
        FactType impClass = kb.getFactType("org.test","Imp");
        TraitableBean imp = (TraitableBean) impClass.newInstance();
                impClass.set(imp, "name", "aaabcd");

            Class trait = kb.getFactType("org.test","Student").getFactClass();
            Class trait2 = kb.getFactType("org.test","Role").getFactClass();

            assertNotNull( trait);

            TraitProxy proxy = (TraitProxy) traitBuilder.getProxy(imp, trait);
            Thing thing = traitBuilder.getProxy(imp, Thing.class);

            TraitableBean core = (TraitableBean) proxy.getObject();


            TraitProxy proxy2 = (TraitProxy) traitBuilder.getProxy(imp, trait);
            Thing thing2 = traitBuilder.getProxy(imp, Thing.class);

            assertSame(proxy,proxy2);
            assertSame(thing,thing2);

            assertEquals(2, core.getTraits().size());


        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }
}
