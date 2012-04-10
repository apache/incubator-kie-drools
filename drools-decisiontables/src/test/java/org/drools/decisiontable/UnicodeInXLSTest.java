package org.drools.decisiontable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.DecisionTableInputType;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.command.Command;
import org.drools.command.CommandFactory;
import org.drools.compiler.DecisionTableFactory;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;
import org.junit.Ignore;

@Ignore
public class UnicodeInXLSTest {

	@Test
    public void testCzechXLSDecisionTable() throws FileNotFoundException {

        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType(DecisionTableInputType.XLS);
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("unicode.xls", getClass()), ResourceType.DTABLE, dtconf);
        if (kbuilder.hasErrors()) {
            System.out.println(kbuilder.getErrors().toString());
            System.out.println(DecisionTableFactory.loadFromInputStream(getClass().getResourceAsStream("unicode.xls"), dtconf));
            fail("Cannot build XLS decision table containing utf-8 characters.");
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        
        List<Command<?>> commands = new ArrayList<Command<?>>();
        List<Člověk> dospělí = new ArrayList<Člověk>();
        commands.add(CommandFactory.newSetGlobal("dospělí", dospělí));
        Člověk Řehoř = new Člověk();
        Řehoř.setVěk(30);
        Řehoř.setJméno("Řehoř");
        commands.add(CommandFactory.newInsert(Řehoř));
        commands.add(CommandFactory.newFireAllRules());

        ksession.execute(CommandFactory.newBatchExecution(commands));

        // people with age greater than 18 should be added to list of adults
        assertNotNull(kbase.getRule("org.drools.decisiontable", "přidej k dospělým"));
        assertEquals(dospělí.size(), 1);
        assertEquals(dospělí.iterator().next().getJméno(), "Řehoř");

        ksession.dispose();
    }
	
    public static class Člověk {

        private int věk;
        private String jméno;

        public void setVěk(int věk) {
            this.věk = věk;
        }

        public int getVěk() {
            return věk;
        }

        public void setJméno(String jméno) {
            this.jméno = jméno;
        }

        public String getJméno() {
            return jméno;
        }
    }
}
