package org.drools.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.drools.Cheese;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.command.Command;
import org.drools.command.CommandFactory;
import org.drools.command.runtime.rule.FireAllRulesCommand;
import org.drools.io.ResourceFactory;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.StatelessKnowledgeSession;
import org.junit.Test;

public class FireAllRulesCommandTest {
    @Test
    public void oneRuleFiredTest() {
        String str = "";
        str += "package org.drools \n";
        str += "rule StringRule \n";
        str += " when \n";
        str += "  $c : Cheese() \n";
        str += " then \n";
        str += "  System.out.println($c); \n";
        str += "end \n";

        StatelessKnowledgeSession ksession = getSession(str);
        
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newInsert(new Cheese("stilton")));
        commands.add(CommandFactory.newFireAllRules("num-rules-fired"));
        
        ExecutionResults results = ksession.execute(CommandFactory.newBatchExecution(commands));
        int fired = Integer.parseInt(results.getValue("num-rules-fired").toString()); 
        
        assertEquals(1, fired);
    }
    
    @Test
    public void fiveRulesFiredTest() {
        String str = "";
        str += "package org.drools \n";
        str += "rule StringRule \n";
        str += " when \n";
        str += "  $c : Cheese() \n";
        str += " then \n";
        str += "  System.out.println($c); \n";
        str += "end \n";

        StatelessKnowledgeSession ksession = getSession(str);
        
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newInsert(new Cheese("stilton")));
        commands.add(CommandFactory.newInsert(new Cheese("gruyere")));
        commands.add(CommandFactory.newInsert(new Cheese("cheddar")));
        commands.add(CommandFactory.newInsert(new Cheese("stinky")));
        commands.add(CommandFactory.newInsert(new Cheese("limburger")));
        commands.add(CommandFactory.newFireAllRules("num-rules-fired"));
        
        ExecutionResults results = ksession.execute(CommandFactory.newBatchExecution(commands));
        int fired = Integer.parseInt(results.getValue("num-rules-fired").toString()); 
        
        assertEquals(5, fired);
    }    
    
    @Test
    public void zeroRulesFiredTest() {
        String str = "";
        str += "package org.drools \n";
        str += "rule StringRule \n";
        str += " when \n";
        str += "  $c : Cheese() \n";
        str += " then \n";
        str += "  System.out.println($c); \n";
        str += "end \n";

        StatelessKnowledgeSession ksession = getSession(str);
        
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newInsert("not cheese"));
        commands.add(CommandFactory.newFireAllRules("num-rules-fired"));
        
        ExecutionResults results = ksession.execute(CommandFactory.newBatchExecution(commands));
        int fired = Integer.parseInt(results.getValue("num-rules-fired").toString()); 
        
        assertEquals(0, fired);        
    }
    
    @Test
    public void oneRuleFiredWithDefinedMaxTest() {
        String str = "";
        str += "package org.drools \n";
        str += "rule StringRule \n";
        str += " when \n";
        str += "  $c : Cheese() \n";
        str += " then \n";
        str += "  System.out.println($c); \n";
        str += "end \n";

        StatelessKnowledgeSession ksession = getSession(str);
        
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newInsert(new Cheese("stilton")));
        FireAllRulesCommand farc = (FireAllRulesCommand) CommandFactory.newFireAllRules(10);
        farc.setOutIdentifier("num-rules-fired");
        commands.add(farc);
        
        ExecutionResults results = ksession.execute(CommandFactory.newBatchExecution(commands));
        int fired = Integer.parseInt(results.getValue("num-rules-fired").toString()); 
        
        assertEquals(1, fired);
    }
    
    @Test
    public void infiniteLoopTerminatesAtMaxTest() {
        String str = "";
        str += "package org.drools \n";
        str += "rule StringRule \n";
        str += " when \n";
        str += "  $c : Cheese() \n";
        str += " then \n";
        str += "  update($c); \n";
        str += "end \n";

        StatelessKnowledgeSession ksession = getSession(str);
        
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newInsert(new Cheese("stilton")));
        FireAllRulesCommand farc = (FireAllRulesCommand) CommandFactory.newFireAllRules(10);
        farc.setOutIdentifier("num-rules-fired");
        commands.add(farc);
        
        ExecutionResults results = ksession.execute(CommandFactory.newBatchExecution(commands));
        int fired = Integer.parseInt(results.getValue("num-rules-fired").toString()); 
        
        assertEquals(10, fired);     
    }

    private StatelessKnowledgeSession getSession(String drl) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add(ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);

        if (kbuilder.hasErrors()) {
            System.err.println(kbuilder.getErrors());
        }
        assertFalse(kbuilder.hasErrors());

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        return kbase.newStatelessKnowledgeSession();
    }

}
