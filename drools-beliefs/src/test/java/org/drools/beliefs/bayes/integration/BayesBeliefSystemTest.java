/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.beliefs.bayes.integration;

import org.drools.beliefs.bayes.BayesBeliefSystem;
import org.drools.beliefs.bayes.BayesInstance;
import org.drools.beliefs.bayes.BayesModeFactory;
import org.drools.beliefs.bayes.BayesModeFactoryImpl;
import org.drools.beliefs.bayes.PropertyReference;
import org.drools.beliefs.bayes.runtime.BayesRuntime;
import org.drools.core.BeliefSystemType;
import org.drools.core.RuleSessionConfiguration;
import org.drools.kiesession.entrypoints.NamedEntryPoint;
import org.drools.core.common.TruthMaintenanceSystemFactory;
import org.drools.core.impl.RuleBaseFactory;
import org.drools.base.rule.EntryPointId;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class BayesBeliefSystemTest {

    @Test
    public void testBayes() {
        String drl = "package org.drools.defeasible; " +
                     "import " + Garden.class.getCanonicalName() + "; \n"  +
                     "import " + PropertyReference.class.getCanonicalName() + "; \n"  +
                     "global " +  BayesModeFactory.class.getCanonicalName() + " bsFactory; \n" +
                     "dialect 'mvel'; \n" +
                     " " +
                     "rule rule1 when " +
                     "        String( this == 'rule1') \n" +
                     "    g : Garden()" +
                     "then " +
                      "    System.out.println(\"rule 1\"); \n" +
                     "    insertLogical( new PropertyReference(g, 'cloudy'), bsFactory.create( new double[] {1.0,0.0} ) ); \n " +
                     "end " +

                     "rule rule2 when " +
                     "        String( this == 'rule2') \n" +
                     "    g : Garden()" +
                     "then " +
                     "    System.out.println(\"rule2\"); \n" +
                     "    insertLogical( new PropertyReference(g, 'sprinkler'), bsFactory.create( new double[] {1.0,0.0} ) ); \n " +
                     "end " +

                     "rule rule3 when " +
                     "        String( this == 'rule3') \n" +
                     "    g : Garden()" +
                     "then " +
                     "    System.out.println(\"rule3\"); \n" +
                     "    insertLogical( new PropertyReference(g, 'sprinkler'), bsFactory.create( new double[] {1.0,0.0} ) ); \n " +
                     "end " +


                     "rule rule4 when " +
                     "        String( this == 'rule4') \n" +
                     "    g : Garden()" +
                     "then " +
                     "    System.out.println(\"rule4\"); \n" +
                     "    insertLogical( new PropertyReference(g, 'sprinkler'), bsFactory.create( new double[] {0.0,1.0} ) ); \n " +
                     "end " +
                     "\n";

        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl) getSessionFromString( drl );

        NamedEntryPoint ep = (NamedEntryPoint) ksession.getEntryPoint(EntryPointId.DEFAULT.getEntryPointId());

        BayesBeliefSystem bayesBeliefSystem = new BayesBeliefSystem( ep, TruthMaintenanceSystemFactory.get().getOrCreateTruthMaintenanceSystem(ep));

        BayesModeFactoryImpl bayesModeFactory = new BayesModeFactoryImpl(bayesBeliefSystem);

        ksession.setGlobal( "bsFactory", bayesModeFactory);

        BayesRuntime bayesRuntime = ksession.getKieRuntime(BayesRuntime.class);
        BayesInstance<Garden> instance = bayesRuntime.createInstance(Garden.class);
        assertThat(instance).isNotNull();

        assertThat(instance.isDecided()).isTrue();
        instance.globalUpdate();
        Garden garden = instance.marginalize();
        assertThat(garden.isWetGrass()).isTrue();

        FactHandle fh = ksession.insert( garden );
        FactHandle fh1 = ksession.insert( "rule1" );
        ksession.fireAllRules();
        assertThat(instance.isDecided()).isTrue();
        instance.globalUpdate(); // rule1 has added evidence, update the bayes network
        garden = instance.marginalize();
        assertThat(garden.isWetGrass()).isTrue();  // grass was wet before rule1 and continues to be wet


        FactHandle fh2 = ksession.insert( "rule2" ); // applies 2 logical insertions
        ksession.fireAllRules();
        assertThat(instance.isDecided()).isTrue();
        instance.globalUpdate();
        garden = instance.marginalize();
        assertThat(garden.isWetGrass() ).isFalse();  // new evidence means grass is no longer wet

        FactHandle fh3 = ksession.insert( "rule3" ); // adds an additional support for the sprinkler, belief set of 2
        ksession.fireAllRules();
        assertThat(instance.isDecided()).isTrue();
        instance.globalUpdate();
        garden = instance.marginalize();
        assertThat(garden.isWetGrass() ).isFalse(); // nothing has changed

        FactHandle fh4 = ksession.insert( "rule4" ); // rule4 introduces a conflict, and the BayesFact becomes undecided
        ksession.fireAllRules();

        assertThat(instance.isDecided()).isFalse();
        try {
            instance.globalUpdate();
            fail( "The BayesFact is undecided, it should throw an exception, as it cannot be updated." );
        } catch ( Exception e ) {
            // this should fail
        }

        ksession.delete( fh4 ); // the conflict is resolved, so it should be decided again
        ksession.fireAllRules();
        assertThat(instance.isDecided()).isTrue();
        instance.globalUpdate();
        garden = instance.marginalize();
        assertThat(garden.isWetGrass() ).isFalse();// back to grass is not wet


        ksession.delete( fh2 ); // takes the sprinkler belief set back to 1
        ksession.fireAllRules();
        instance.globalUpdate();
        garden = instance.marginalize();
        assertThat(garden.isWetGrass() ).isFalse(); // still grass is not wet

        ksession.delete( fh3 ); // no sprinkler support now
        ksession.fireAllRules();
        instance.globalUpdate();
        garden = instance.marginalize();
        assertThat(garden.isWetGrass()).isTrue(); // grass is wet again
    }

    protected KieSession getSessionFromString( String drlString) {
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add( ResourceFactory.newByteArrayResource(drlString.getBytes()),
                      ResourceType.DRL );

        kBuilder.add( ResourceFactory.newClassPathResource("Garden.xmlbif", AssemblerTest.class), ResourceType.BAYES );

        if ( kBuilder.hasErrors() ) {
            System.err.println( kBuilder.getErrors() );
            fail("Unexpected errors");
        }

        InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        kBase.addPackages( kBuilder.getKnowledgePackages() );

        KieSessionConfiguration ksConf = RuleBaseFactory.newKnowledgeSessionConfiguration();
        ksConf.as(RuleSessionConfiguration.KEY).setBeliefSystemType(BeliefSystemType.DEFEASIBLE);

        KieSession kSession = kBase.newKieSession( ksConf, null );
        return kSession;
    }
}
