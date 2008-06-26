package org.drools.examples.ruleflow;

import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.drools.process.instance.impl.demo.UIWorkItemHandler;
import org.drools.rule.Package;

public class WorkItemExample {

    public static final void main(String[] args) {
        try {
            RuleBase ruleBase = readRule();
            WorkingMemory workingMemory = ruleBase.newStatefulSession();
            
            // logging all work items to sysout
            SystemOutWorkItemHandler handler = new SystemOutWorkItemHandler();
            workingMemory.getWorkItemManager().registerWorkItemHandler("Email", handler);
            workingMemory.getWorkItemManager().registerWorkItemHandler("Log", handler);
            
            // using a dialog to show all work items
            UIWorkItemHandler handler2 = new UIWorkItemHandler();
            //workingMemory.getWorkItemManager().registerWorkItemHandler("Email", handler2);
            //workingMemory.getWorkItemManager().registerWorkItemHandler("Log", handler2);
            //handler2.setVisible(true);
            
            workingMemory.startProcess("com.sample.ruleflow");
            workingMemory.fireAllRules();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

	private static RuleBase readRule() throws Exception {
		PackageBuilder builder = new PackageBuilder();
		Reader source = new InputStreamReader( WorkItemExample.class.getResourceAsStream( "/org/drools/examples/ruleflow/workitems.rf" ) );
		builder.addRuleFlow(source);
		Package pkg = builder.getPackage();
		RuleBase ruleBase = RuleBaseFactory.newRuleBase();
		ruleBase.addPackage( pkg );
		return ruleBase;
	}
	
}
