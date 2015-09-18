/*
 * Copyright 2015 JBoss Inc
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
 *
 */

package org.drools.compiler.integrationtests;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Message;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class DynamicRuleLoadTest extends CommonTestMethodBase {

    private final String drl1 =
            "package org.drools.compiler\n" +
            "rule R1 when\n" +
            "   Message( $m : message )\n" +
            "then\n" +
            "    System.out.println($m);\n" +
            "end\n";

    private final String drl2_1 =
            "package org.drools.compiler\n" +
            "global " + DynamicRuleLoadTest.class.getCanonicalName() + " test;\n" +
            "rule R2_1 when\n" +
            "   $m : Message( message == \"Hi Universe\" )\n" +
            "then\n" +
            "    test.updateToVersion();" +
            "end\n";

    private final String drl2_2 =
            "package org.drools.compiler\n" +
            "global " + DynamicRuleLoadTest.class.getCanonicalName() + " test;\n" +
            "rule R2_2 when\n" +
            "   $m : Message( message == \"Hello World\" )\n" +
            "then\n" +
            "    test.done();" +
            "end\n";

    private KieContainer kieContainer;
    private KieSession ksession;

    private boolean done = false;

    @Test
    public void testKJarUpgrade() throws Exception {
        // DROOLS-919
        KieServices ks = KieServices.Factory.get();

        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        KieModule km = createAndDeployJar( ks, releaseId1, drl1, drl2_1 );

        // Create a session and fire rules
        kieContainer = ks.newKieContainer( km.getReleaseId() );
        ksession = kieContainer.newKieSession();

        ksession.setGlobal( "test", this );
        ksession.insert( new Message( "Hi Universe" ) );
        ksession.fireAllRules();

        assertTrue( done );
    }

    public void updateToVersion() {
        KieServices ks = KieServices.Factory.get();

        // Create a new jar for version 1.1.0
        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        KieModule km = createAndDeployJar( ks, releaseId2, drl1, drl2_2 );

        // try to update the container to version 1.1.0
        kieContainer.updateToVersion( releaseId2 );

        // create and use a new session
        ksession.insert( new Message( "Hello World" ) );
    }

    public void done() {
        done = true;
    }
}
