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
package org.drools.mvel.integrationtests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.time.SessionPseudoClock;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class QueryInRHSCepTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public QueryInRHSCepTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseStreamConfigurations(true);
    }
	private KieSession ksession;
    private SessionPseudoClock clock;
	private List<?> myGlobal;
	
    public static class QueryItemPojo {
    	// empty pojo.
	}

	public static class SolicitFirePojo {
    	// empty pojo.
	}
	
    private void prepare1() {
        String drl = "package org.drools.mvel.integrationtests\n" +
                "import " + SolicitFirePojo.class.getCanonicalName() + "\n" +
                "import " + QueryItemPojo.class.getCanonicalName() + "\n" +
                "global java.util.List myGlobal \n"+
                "declare SolicitFirePojo\n" +
                "    @role( event )\n" + 
                "end\n" + 
                "query \"myQuery\"\n" + 
                "    $r : QueryItemPojo()\n" + 	
                "end\n" + 
                "rule \"drools-usage/WLHxG8S\"\n" +
                " no-loop\n" +
                " when\n" +
                " SolicitFirePojo()\n" +
                " then\n" +
                " myGlobal.add(drools.getKieRuntime().getQueryResults(\"myQuery\"));\n"+
                " end\n";
        
        final KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        KieModuleModel kmodule = ks.newKieModuleModel();

        KieBaseModel baseModel = kmodule.newKieBaseModel("defaultKBase")
                .setDefault(true)
                .setEventProcessingMode(EventProcessingOption.STREAM);
        baseModel.newKieSessionModel("defaultKSession")
                .setDefault(true)
                .setClockType(ClockTypeOption.PSEUDO);

        kfs.writeKModuleXML(kmodule.toXML());
        kfs.write( ResourceFactory.newByteArrayResource(drl.getBytes())
                                  .setTargetPath("org/drools/compiler/integrationtests/"+this.getClass().getName()+".drl") );

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, true);
        ksession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();
        
        clock = ksession.getSessionClock();
        myGlobal = new ArrayList<>();
        ksession.setGlobal("myGlobal", myGlobal);
    }

    @Test
    public void withResultOfSize1Test() {
    	prepare1();
    	clock.advanceTime(1, TimeUnit.SECONDS);
        ksession.insert(new QueryItemPojo());
        ksession.insert(new SolicitFirePojo());
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(1);
        assertThat(myGlobal.size()).isEqualTo(1);
        assertThat(((QueryResults) myGlobal.get(0)).size()).isEqualTo(1);
    }
    @Test
    public void withResultOfSize1AnotherTest() {
    	prepare1();
    	clock.advanceTime(1, TimeUnit.SECONDS);
        ksession.insert(new SolicitFirePojo());
        ksession.insert(new QueryItemPojo());
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(1);
        assertThat(myGlobal.size()).isEqualTo(1);
        assertThat(((QueryResults) myGlobal.get(0)).size()).isEqualTo(1);
    }
    @Test
    public void withResultOfSize0Test() {
    	prepare1();
    	clock.advanceTime(1, TimeUnit.SECONDS);
        ksession.insert(new SolicitFirePojo());
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(1);
        assertThat(myGlobal.size()).isEqualTo(1);
        assertThat(((QueryResults) myGlobal.get(0)).size()).isEqualTo(0);
    }
    
    @Test
    public void withInsertBeforeQueryCloudTest() {
    	String drl = "package org.drools.mvel.integrationtests\n" +
                "import " + SolicitFirePojo.class.getCanonicalName() + "\n" +
                "import " + QueryItemPojo.class.getCanonicalName() + "\n" +
                "global java.util.List myGlobal \n"+
                "query \"myQuery\"\n" + 
                "    $r : QueryItemPojo()\n" + 	
                "end\n" + 
                "rule \"drools-usage/WLHxG8S\"\n" +
                " no-loop\n" +
                " when\n" +
                " SolicitFirePojo()\n" +
                " then\n" +
                " insert(new QueryItemPojo());\n" +
                " myGlobal.add(drools.getKieRuntime().getQueryResults(\"myQuery\"));\n"+
                " end\n";

        final KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        KieModuleModel kmodule = ks.newKieModuleModel();

        KieBaseModel baseModel = kmodule.newKieBaseModel("defaultKBase")
                .setDefault(true)
                .setEventProcessingMode(EventProcessingOption.CLOUD)
                ;
        baseModel.newKieSessionModel("defaultKSession")
                .setDefault(true)
                ;

        kfs.writeKModuleXML(kmodule.toXML());
        kfs.write( ResourceFactory.newByteArrayResource(drl.getBytes())
                                  .setTargetPath("org/drools/compiler/integrationtests/"+this.getClass().getName()+".drl") );

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, true);
        ksession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();
        
        myGlobal = new ArrayList<>();
        ksession.setGlobal("myGlobal", myGlobal);
        
        ksession.insert(new QueryItemPojo());
        ksession.insert(new SolicitFirePojo());
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(1);
        assertThat(myGlobal.size()).isEqualTo(1);
        assertThat(((QueryResults) myGlobal.get(0)).size()).isEqualTo(2); // notice 1 is manually inserted, 1 get inserted from rule's RHS, for a total of 2.
    }

    @Test(timeout = 10000L)
    public void testParallelQueryCallFromRuleAndAPI() {
        String drl =
                "global java.util.List myGlobal \n"+
                "query \"myQuery\"\n" +
                "    $r : String()\n" +
                "end\n" +
                "rule R when\n" +
                "  $i : Integer()\n" +
                "then\n" +
                "  insert($i.toString());\n" +
                "  myGlobal.add(drools.getKieRuntime().getQueryResults(\"myQuery\"));\n"+
                "end\n";

        KieBaseTestConfiguration cloudConfig = TestParametersUtil.getCloudInstanceOf(kieBaseTestConfiguration);
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", cloudConfig, drl);
        KieSession kSession = kbase.newKieSession();

        myGlobal = new ArrayList<>();
        kSession.setGlobal("myGlobal", myGlobal);

        int threadCount = 4;
        int iterations = 1000;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        try {
            final Callable[] tasks = new Callable[threadCount];
            for (int i = 0; i < threadCount; i++) {
                tasks[i] = createTask( kSession, iterations );
            }

            final CompletionService<Boolean> ecs = new ExecutorCompletionService<>( executor );
            for (final Callable task : tasks) {
                ecs.submit( task );
            }

            for (int i = 1; i <= iterations; i++) {
                kSession.insert( i );
                kSession.fireAllRules();
                assertThat(myGlobal.size()).isEqualTo(1);
                assertThat(((QueryResults) myGlobal.get(0)).size()).isEqualTo(i);
                myGlobal.clear();
            }

            int successCounter = 0;
            for (int i = 0; i < threadCount; i++) {
                try {
                    if ( ecs.take().get() ) {
                        successCounter++;
                    }
                } catch (final Exception e) {
                    throw new RuntimeException( e );
                }
            }

            assertThat(threadCount).isEqualTo(successCounter);
        } finally {
            kSession.dispose();
            executor.shutdownNow();
        }
    }

    private Callable<Boolean> createTask(KieSession kSession, int iterations) {
        return () -> {
            int currentValue = 0;
            for (int i = 0; i < iterations; i++) {
                QueryResults queryResults = kSession.getQueryResults( "myQuery" );
                int newValue = queryResults.size();
                if (newValue < currentValue) {
                    return false;
                }
                currentValue = newValue;
            }
            return true;
        };
    }
}
