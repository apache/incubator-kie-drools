package org.drools.compiler.integrationtests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Message.Level;
import org.kie.api.runtime.KieSession;
import org.kie.api.conf.DeclarativeAgendaOption;

public class AgendaGroupSalienceTest {
    
    private KieSession ksession;
    
    @Before
    public void initialization() {
        
        KieServices kieServices = KieServices.Factory.get();
        
        KieFileSystem kfs = kieServices.newKieFileSystem();

        kfs.write(kieServices.getResources().newClassPathResource("agenda_group_salience.drl", AgendaGroupSalienceTest.class));

        KieBuilder kbuilder = kieServices.newKieBuilder(kfs);

        kbuilder.buildAll();
        
        List<Message> res = kbuilder.getResults().getMessages(Level.ERROR);
        
        assertEquals(res.toString(), 0, res.size());
        
        KieBaseConfiguration kbconf = kieServices.newKieBaseConfiguration();
        kbconf.setOption(DeclarativeAgendaOption.ENABLED);
        
        KieBase kbase = kieServices.newKieContainer(kbuilder.getKieModule().getReleaseId()).newKieBase(kbconf);
        
        ksession = kbase.newKieSession();
    }
    
    @Ignore
    @Test(timeout = 60000L)
    public void test() {
        
        ArrayList<String> ruleList = new ArrayList<String>();

        ksession.setGlobal("ruleList", ruleList);
        
        ksession.insert("fireRules");
        
        ksession.fireAllRules();

        assertEquals(ruleList.get(0), "first");
        assertEquals(ruleList.get(1), "second");
        assertEquals(ruleList.get(2), "third");    
    }
}
