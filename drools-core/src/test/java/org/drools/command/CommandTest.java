package org.drools.command;

import java.util.ArrayList;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.StatefulKnowledgeSession;


public class CommandTest {
    public void test1() {
        List<Command> cmds = new ArrayList<Command>();
        
        BatchExecutionCommand batch = CommandFactory.newBatchExecution( cmds );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        
        ExecutionResults results = ksession.execute( batch );
        
    }
}
