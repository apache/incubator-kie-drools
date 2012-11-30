package org.drools.integrationtests;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.drools.CommonTestMethodBase;
import org.drools.Message;
import org.drools.base.mvel.MVELDebugHandler;
import org.junit.Test;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KieBuilder;
import org.kie.builder.KieContainer;
import org.kie.builder.KieFactory;
import org.kie.builder.KieFileSystem;
import org.kie.builder.KieJar;
import org.kie.builder.KieProject;
import org.kie.builder.KieRepository;
import org.kie.builder.KieServices;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.kie.io.ResourceFactory;
import org.kie.logger.KnowledgeRuntimeLogger;
import org.kie.logger.KnowledgeRuntimeLoggerFactory;
import org.kie.runtime.KieBase;
import org.kie.runtime.KieSession;
import org.kie.runtime.StatefulKnowledgeSession;
import org.mvel2.MVELRuntime;
import org.mvel2.debug.Debugger;
import org.mvel2.debug.Frame;

/**
 * This is a sample class to launch a rule.
 */
public class KieHelloWorldTest extends CommonTestMethodBase {

    @Test
    public void testHelloWorld() throws Exception {
        String drl = "package org.drools\n" +
                "rule R1 when\n" +
                "   $m : Message( )\n" +
                "then\n" +
                "end\n";
        
        KieServices ks = KieServices.Factory.get();
        KieFactory kf = KieFactory.Factory.get();
        
        KieFileSystem kfs = kf.newKieFileSystem()
                              .write( "src/main/resources/org/domain/r1.drl", drl );
        
        KieBuilder kb = ks.newKieBuilder( kfs );
        kb.build();
        
        KieContainer kc = ks.getKieContainer( );
        KieBase kieBase = kc.getKieBase();
        KieSession ksession = kieBase.newKieSession();
        

        int count = ksession.fireAllRules();
         
        assertEquals( 1, count );
    }


}
