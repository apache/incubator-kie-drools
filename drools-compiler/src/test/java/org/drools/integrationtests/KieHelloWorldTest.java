package org.drools.integrationtests;

import org.drools.CommonTestMethodBase;
import org.drools.Message;
import org.junit.Test;
import org.kie.builder.KieFactory;
import org.kie.builder.KieFileSystem;
import org.kie.builder.KieServices;
import org.kie.runtime.KieSession;

/**
 * This is a sample class to launch a rule.
 */
public class KieHelloWorldTest extends CommonTestMethodBase {

    @Test
    public void testHelloWorld() throws Exception {
        String drl = "package org.drools\n" +
                "rule R1 when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";
        
        KieServices ks = KieServices.Factory.get();
        KieFactory kf = KieFactory.Factory.get();
        
        KieFileSystem kfs = kf.newKieFileSystem().write( "r1.drl", drl );
        ks.newKieBuilder( kfs ).build();

        KieSession ksession = ks.getKieContainer().getKieSession();
        ksession.insert(new Message("Hello World"));

        int count = ksession.fireAllRules();
         
        assertEquals( 1, count );
    }


}
