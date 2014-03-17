package org.drools.persistence.session;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.drools.compiler.integrationtests.DeleteTest;
import org.drools.persistence.util.PersistenceUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Message.Level;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import static org.drools.persistence.util.PersistenceUtil.*;
import static org.junit.Assert.*;

public class FireRulesWithListenerTest {

    private HashMap<String, Object> context;
    private Environment env;

    @Before
    public void setUp() throws Exception {
        context = PersistenceUtil.setupWithPoolingDataSource(DROOLS_PERSISTENCE_UNIT_NAME);
        env = createEnvironment(context);
    }

    @After
    public void tearDown() throws Exception {
        PersistenceUtil.cleanUp(context);
    }

    @Test
    public void testFireRulesWithListener() throws Exception {



        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();

        kfs.write(KieServices.Factory.get().getResources()
                .newClassPathResource("org/drools/persistence/fire-rules-with-listener.drl", DeleteTest.class));

        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder(kfs);

        kbuilder.buildAll();

        List<Message> res = kbuilder.getResults().getMessages(Level.ERROR);

        assertEquals(res.toString(), 0, res.size());

        KieBase kbase = KieServices.Factory.get()
                .newKieContainer(kbuilder.getKieModule().getReleaseId())
                .getKieBase();

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);

        ksession.addEventListener(new TriggerRuleEventListener(ksession));

        Account accountOpen = new Account("O");
        Account accountClosed = new Account("C");

        FactHandle accountOpen_FH = ksession.insert( accountOpen );
        FactHandle accountClosed_FH = ksession.insert(accountClosed);

        SimpleDateFormat stdDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date today = stdDateFormat.parse("2014/03/10");
        Date yesterday = stdDateFormat.parse("2014/03/09");
        Date lastYear = stdDateFormat.parse("2013/03/10");

        OrderDetails lastYearOrder = new OrderDetails(lastYear, today);
        OrderDetails thisYearOrder = new OrderDetails(yesterday, today);

        OrderEligibility lastYearEligibility = new OrderEligibility(lastYearOrder);
        OrderEligibility thisYearEligibility = new OrderEligibility(thisYearOrder);

        FactHandle lye_FH = ksession.insert( lastYearEligibility );
        FactHandle tye_FH = ksession.insert(thisYearEligibility);

        ksession.fireAllRules();

        assertTrue(accountOpen.getAccountEligible());
        assertTrue(!accountClosed.getAccountEligible());

        ksession.delete(accountOpen_FH);
        ksession.delete(accountClosed_FH);

        assertTrue(lastYearEligibility.getOrderEligibile());
        assertTrue(!thisYearEligibility.getOrderEligibile());
        ksession.delete(lye_FH);
        ksession.delete(tye_FH);

        ksession.dispose();
    }

    private static class TriggerRuleEventListener extends DefaultAgendaEventListener {
        private KieSession ksession;

        public TriggerRuleEventListener(KieSession ksession) {

            this.ksession = ksession;
        }

        @Override
        public void matchCreated(MatchCreatedEvent event) {
            ksession.fireAllRules();
        }
    }
}
