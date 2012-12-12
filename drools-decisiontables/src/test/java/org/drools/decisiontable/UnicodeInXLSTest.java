package org.drools.decisiontable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.DecisionTableFactory;
import org.junit.Test;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.DecisionTableConfiguration;
import org.kie.builder.DecisionTableInputType;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.command.Command;
import org.kie.command.CommandFactory;
import org.kie.io.ResourceFactory;
import org.kie.io.ResourceType;
import org.kie.runtime.StatefulKnowledgeSession;

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
            fail("Cannot build XLS decision table containing utf-8 characters\n" + kbuilder.getErrors().toString() );
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
