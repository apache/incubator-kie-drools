package org.drools.decisiontable;

import java.util.ArrayList;
import java.util.List;

import org.drools.drl.extensions.DecisionTableFactory;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.junit.After;
import org.junit.Test;
import org.kie.api.command.Command;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class UnicodeInCSVTest {
    
    private KieSession ksession;

    @After
    public void tearDown() {
        if (ksession != null) {
            ksession.dispose();
        }
    }

    @Test
    public void testUnicodeCSVDecisionTable() {

        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType(DecisionTableInputType.CSV);
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("unicode.drl.csv", getClass()), ResourceType.DTABLE, dtconf);
        if (kbuilder.hasErrors()) {
            System.out.println(kbuilder.getErrors().toString());
            System.out.println(DecisionTableFactory.loadFromInputStream(getClass().getResourceAsStream("unicode.drl.xls"), dtconf));
            fail("Cannot build CSV decision table containing utf-8 characters\n" + kbuilder.getErrors().toString() );
        }
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        
        ksession = kbase.newKieSession();
        
        List<Command<?>> commands = new ArrayList<>();
        List<Člověk> dospělí = new ArrayList<>();
        commands.add(CommandFactory.newSetGlobal("dospělí", dospělí));
        Člověk Řehoř = new Člověk();
        Řehoř.setVěk(30);
        Řehoř.setJméno("Řehoř");
        commands.add(CommandFactory.newInsert(Řehoř));
        commands.add(CommandFactory.newFireAllRules());

        ksession.execute(CommandFactory.newBatchExecution(commands));

        // people with age greater than 18 should be added to list of adults
        assertThat(kbase.getRule("org.drools.decisiontable", "přidej k dospělým")).isNotNull();
        assertThat(dospělí).hasSize(5);
        assertThat(dospělí.iterator().next().getJméno()).isEqualTo("Řehoř");

        assertThat(kbase.getRule("org.drools.decisiontable", "привет мир")).isNotNull();
        assertThat(kbase.getRule("org.drools.decisiontable", "你好世界")).isNotNull();
        assertThat(kbase.getRule("org.drools.decisiontable", "hallå världen")).isNotNull();
        assertThat(kbase.getRule("org.drools.decisiontable", "مرحبا العالم")).isNotNull();
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
