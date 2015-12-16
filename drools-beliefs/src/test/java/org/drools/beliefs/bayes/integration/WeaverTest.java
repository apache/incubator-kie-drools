/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.beliefs.bayes.integration;

import org.drools.beliefs.bayes.JunctionTree;
import org.drools.beliefs.bayes.assembler.BayesPackage;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.kie.internal.io.ResourceTypePackage;
import org.junit.Test;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.conf.RuleEngineOption;
import org.kie.internal.io.ResourceFactory;

import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class WeaverTest {

    @Test
    public void testBayesPackageWeaving() throws Exception {
        KnowledgeBuilderImpl kbuilder = new KnowledgeBuilderImpl();
        kbuilder.add( ResourceFactory.newClassPathResource("Garden.xmlbif", AssemblerTest.class), ResourceType.BAYES );


        KnowledgeBase kbase = getKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        InternalKnowledgePackage kpkg = (InternalKnowledgePackage) kbase.getKiePackage("org.drools.beliefs.bayes.integration");
        Map<ResourceType, ResourceTypePackage> map = kpkg.getResourceTypePackages();
        BayesPackage existing  = (BayesPackage) map.get( ResourceType.BAYES );
        JunctionTree jtree =  existing.getJunctionTree("Garden");
        assertNotNull( jtree );
    }

    protected KnowledgeBase getKnowledgeBase() {
        KieBaseConfiguration kBaseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kBaseConfig.setOption(RuleEngineOption.PHREAK);
        return getKnowledgeBase(kBaseConfig);
    }

    protected KnowledgeBase getKnowledgeBase(KieBaseConfiguration kBaseConfig) {
        kBaseConfig.setOption(RuleEngineOption.PHREAK);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kBaseConfig);
        return kbase;
    }



}
