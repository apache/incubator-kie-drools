package org.drools.factmodel.traits;

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

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.base.DefaultKnowledgeHelper;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.common.AbstractRuleBase;
import org.drools.definition.type.FactType;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.junit.*;

import java.util.Date;
import java.util.Map;

import static junit.framework.Assert.*;

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

    @Before
    public void reset() {
        TraitRegistry.reset();
        TraitFactory.reset();
    }

    @Test
    public void testHasTypes() {

        String source = "org/drools/factmodel/traits/testTraitDon.drl";

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
