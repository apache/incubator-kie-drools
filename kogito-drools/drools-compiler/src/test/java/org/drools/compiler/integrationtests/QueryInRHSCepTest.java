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

package org.drools.compiler.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.drools.core.time.SessionPseudoClock;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.internal.io.ResourceFactory;

public class QueryInRHSCepTest {
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
        String drl = "package org.drools.compiler.integrationtests\n" +
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
                .setClockType(ClockTypeOption.get("pseudo"));

        kfs.writeKModuleXML(kmodule.toXML());
        kfs.write( ResourceFactory.newByteArrayResource(drl.getBytes())
                                  .setTargetPath("org/drools/compiler/integrationtests/"+this.getClass().getName()+".drl") );

        assertTrue(ks.newKieBuilder(kfs).buildAll().getResults().getMessages().isEmpty());
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
        assertEquals(1, fired);
        assertEquals(1, myGlobal.size());
        assertEquals(1, ((QueryResults) myGlobal.get(0)).size());
    }
    @Test
    public void withResultOfSize1AnotherTest() {
    	prepare1();
    	clock.advanceTime(1, TimeUnit.SECONDS);
        ksession.insert(new SolicitFirePojo());
        ksession.insert(new QueryItemPojo());
        int fired = ksession.fireAllRules();
        assertEquals(1, fired);
        assertEquals(1, myGlobal.size());
        assertEquals(1, ((QueryResults) myGlobal.get(0)).size());
    }
    @Test
    public void withResultOfSize0Test() {
    	prepare1();
    	clock.advanceTime(1, TimeUnit.SECONDS);
        ksession.insert(new SolicitFirePojo());
        int fired = ksession.fireAllRules();
        assertEquals(1, fired);
        assertEquals(1, myGlobal.size());
        assertEquals(0, ((QueryResults) myGlobal.get(0)).size());
    }
    
    @Test
    public void withInsertBeforeQueryCloudTest() {
    	String drl = "package org.drools.compiler.integrationtests\n" +
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
        System.out.println(drl);
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

        assertTrue(ks.newKieBuilder(kfs).buildAll().getResults().getMessages().isEmpty());
        ksession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();
        
        myGlobal = new ArrayList<>();
        ksession.setGlobal("myGlobal", myGlobal);
        
        ksession.insert(new QueryItemPojo());
        ksession.insert(new SolicitFirePojo());
        int fired = ksession.fireAllRules();
        assertEquals(1, fired);
        assertEquals(1, myGlobal.size());
        assertEquals(2, ((QueryResults) myGlobal.get(0)).size()); // notice 1 is manually inserted, 1 get inserted from rule's RHS, for a total of 2.
    }
}
