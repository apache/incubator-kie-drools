package org.drools.persistence.kie.persistence.session;

import static org.drools.persistence.util.PersistenceUtil.DROOLS_PERSISTENCE_UNIT_NAME;
import static org.drools.persistence.util.PersistenceUtil.createEnvironment;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.drools.persistence.util.PersistenceUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;

public class InsertLogicalTest {
    
    private HashMap<String, Object> context;
    private Environment env;
    
    @Before
    public void setUp() throws Exception {
        context = PersistenceUtil.setupWithPoolingDataSource(DROOLS_PERSISTENCE_UNIT_NAME);
        env = createEnvironment(context);
    }
    
    @Ignore
    @Test
    public void test() {
        
        String str = "";
        str += "package org.kie.test\n";
        str += "rule rule1\n";
        str += "when\n";
        str += "then\n";
        str += " insertLogical( new String(\"a\") );\n";
        str += "end\n";
        str += "\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", str );
        ks.newKieBuilder( kfs ).buildAll();

        KieBase kbase = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).getKieBase();
        KieSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
        
        assertEquals(1, ksession.fireAllRules());
        assertEquals(1, ksession.getFactCount());  
    }
    
    @After
    public void tearDown() {
        PersistenceUtil.cleanUp(context);
    }
}
