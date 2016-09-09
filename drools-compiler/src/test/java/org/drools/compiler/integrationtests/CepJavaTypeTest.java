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

package org.drools.compiler.integrationtests;

import org.drools.compiler.CommonTestMethodBase;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

public class CepJavaTypeTest extends CommonTestMethodBase {

    @Role(value = Role.Type.EVENT)
    public static class Event { }

    @Test
    public void testJavaTypeAnnotatedWithRole_WindowTime() {
        String drl = "package org.drools.compiler.integrationtests\n"
                + "\n"
                + "import org.drools.compiler.integrationtests.CepJavaTypeTest.Event;\n"
                + "\n"
                + "rule \"CEP Window Time\"\n"
                + "when\n"
                + "    Event() over window:time (1d)\n"
                + "then\n"
                + "end\n";

        KieServices ks = KieServices.Factory.get();

        KieModuleModel module = ks.newKieModuleModel();

        KieBaseModel defaultBase = module.newKieBaseModel( "defaultKBase" )
                .setDefault( true )
                .addPackage( "*" );
        defaultBase.newKieSessionModel( "defaultKSession" )
                .setDefault( true );

        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", drl );
        kfs.writeKModuleXML( module.toXML() );
        KieBuilder builder = ks.newKieBuilder( kfs ).buildAll();

        assertTrue( builder.getResults().getMessages().isEmpty() );
    }

    @Test
    public void testJavaTypeAnnotatedWithRole_WindowLength() {
        String drl = "package org.drools.compiler.integrationtests\n"
                + "\n"
                + "import org.drools.compiler.integrationtests.CepJavaTypeTest.Event;\n"
                + "\n"
                + "rule \"CEP Window Length\"\n"
                + "when\n"
                + "    Event() over window:length (10)\n"
                + "then\n"
                + "end\n";

        KieServices ks = KieServices.Factory.get();

        KieModuleModel module = ks.newKieModuleModel();

        KieBaseModel defaultBase = module.newKieBaseModel( "defaultKBase" )
                .setDefault( true )
                .addPackage( "*" );
        defaultBase.newKieSessionModel( "defaultKSession" )
                .setDefault( true );

        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", drl );
        kfs.writeKModuleXML( module.toXML() );
        KieBuilder builder = ks.newKieBuilder( kfs ).buildAll();

        assertTrue( builder.getResults().getMessages().isEmpty() );
    }

    @Role(value = Role.Type.EVENT)
    @Timestamp( "Ts" )
    @Expires( "1ms" )
    public static class MyMessage {
        String name;
        long ts;

        public MyMessage(String n) {
            name = n;
            ts = System.currentTimeMillis();
        }

        public void setName(String n) { name = n; }
        public String getName() { return name; }
        public void setTs(long t) { ts = t; }
        public long getTs() { return ts; }
    }

    @Test
    public void testEventWithShortExpiration() throws InterruptedException {
        // BZ-1265773
        String drl = "import " + MyMessage.class.getCanonicalName() +"\n" +
                     "rule \"Rule A Start\"\n" +
                     "when\n" +
                     "  MyMessage ( name == \"ATrigger\" )\n" +
                     "then\n" +
                     "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build( EventProcessingOption.STREAM )
                                             .newKieSession();

        ksession.insert(new MyMessage("ATrigger" ) );
        assertEquals( 1, ksession.fireAllRules() );
        waitBusy(2L);
        assertEquals( 0, ksession.fireAllRules() );
        waitBusy(30L);
        // Expire action is put into propagation queue by timer job, so there
        // can be a race condition where it puts it there right after previous fireAllRules
        // flushes the queue. So there needs to be another flush -> another fireAllRules
        // to flush the queue.
        assertEquals( 0, ksession.fireAllRules() );
        assertEquals( 0, ksession.getObjects().size() );
    }
}
