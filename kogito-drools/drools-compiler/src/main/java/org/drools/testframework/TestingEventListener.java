package org.drools.testframework;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.drools.RuleBase;
import org.drools.WorkingMemory;
import org.drools.event.ActivationCancelledEvent;
import org.drools.event.ActivationCreatedEvent;
import org.drools.event.AfterActivationFiredEvent;
import org.drools.event.AgendaEventListener;
import org.drools.event.AgendaGroupPoppedEvent;
import org.drools.event.AgendaGroupPushedEvent;
import org.drools.event.BeforeActivationFiredEvent;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.AgendaFilter;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;

/**
 * This tracks what is happening in the engine with rule activations and firings.
 * It also allows you to choose what to include/exclude from firing.
 *
 * If a rule is not allowed to fire, it will still be counted as an activation.
 * If it is allowed to fire, then it will only be counted after the activation is fired.
 *
 * @author Michael Neale
 */
public class TestingEventListener implements AgendaEventListener {

    final Map	firingCounts = new HashMap(100);

    HashSet	ruleNames = new HashSet();


    /**
     * Exclusive means DO NOT fire the rules mentioned.
     * For those rules, they will still be counted, just not allowed to activate.
     * Inclusive means only the rules on the given set are allowed to fire.
     * The other rules will have their activation counted but not be allowed to fire.
     */
    public TestingEventListener(HashSet ruleNames, RuleBase ruleBase, boolean inclusive) {
    	if (inclusive) {
    		Package[] pkgs = ruleBase.getPackages();
    		for (int i = 0; i < pkgs.length; i++) {
				Rule[] rules = pkgs[i].getRules();
				for (int j = 0; j < rules.length; j++) {
					Rule rule = rules[j];
					if (!ruleNames.contains(rule.getName())) {
						rule.setConsequence(new NilConsequence());
					}
				}
			}
    	} else {
    		Package[] pkgs = ruleBase.getPackages();
    		for (int i = 0; i < pkgs.length; i++) {
    			Package pkg = pkgs[i];
    			for (Iterator iter = ruleNames.iterator(); iter.hasNext();) {
					String name = (String) iter.next();
					Rule rule = pkg.getRule(name);
					rule.setConsequence(new NilConsequence());
				}

    		}
    	}
    	this.ruleNames = ruleNames;
    }





    public void activationCancelled(ActivationCancelledEvent event,
            WorkingMemory workingMemory) {
    }

    public void activationCreated(ActivationCreatedEvent event,
            WorkingMemory workingMemory) {
    }

    public void afterActivationFired(AfterActivationFiredEvent event,
            WorkingMemory workingMemory) {
    	recordFiring(event.getActivation().getRule());
    }





	private void recordFiring(Rule rule) {
		record(rule, this.firingCounts);
	}



	public void agendaGroupPopped(AgendaGroupPoppedEvent event,
            WorkingMemory workingMemory) {
    }

    public void agendaGroupPushed(AgendaGroupPushedEvent event,
            WorkingMemory workingMemory) {
    }

	public void beforeActivationFired(BeforeActivationFiredEvent event, WorkingMemory workingMemory) {

	}





	private void record(Rule rule, Map counts) {
		String name = rule.getName();
		if (!counts.containsKey(name)) {
			counts.put(name, new Integer(1));
		} else {
			Integer count = (Integer) counts.get(name);
			counts.put(name, new Integer(count.intValue() + 1));
		}
	}

	boolean hasRule(Rule rule) {
		return this.ruleNames.contains(rule.getName());
	}



}

class NilConsequence implements Consequence {

	public void evaluate(KnowledgeHelper knowledgeHelper, WorkingMemory workingMemory) throws Exception {
	}
}

