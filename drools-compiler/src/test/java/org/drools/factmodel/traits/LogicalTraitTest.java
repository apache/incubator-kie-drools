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
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import static junit.framework.Assert.*;

public class LogicalTraitTest {

    @Test
    @Ignore
    public void testShadowAliasingTriples() {
        shadowAlias( TraitFactory.VirtualPropertyMode.TRIPLES );
    }

    @Test
    @Ignore
    public void testShadowAliasingMap() {
        shadowAlias( TraitFactory.VirtualPropertyMode.MAP );
    }

    public void shadowAlias( TraitFactory.VirtualPropertyMode mode ) {

        KnowledgeBuilder kbuilderImpl = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilderImpl.add( ResourceFactory.newClassPathResource ( "org/drools/factmodel/traits/testTraitedAliasing.drl" ), ResourceType.DRL );
        if ( kbuilderImpl.hasErrors() ) {
            fail( kbuilderImpl.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilderImpl.getKnowledgePackages() );

        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.TRIPLES, kbase );

        StatefulKnowledgeSession ks = kbase.newStatefulKnowledgeSession();

        ArrayList list = new ArrayList(  );
        ks.setGlobal( "list", list );

        ks.fireAllRules();
        System.out.println( list );
    }



}
