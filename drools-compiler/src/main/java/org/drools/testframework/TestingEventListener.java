package org.drools.testframework;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.WorkingMemory;
import org.drools.event.ActivationCancelledEvent;
import org.drools.event.ActivationCreatedEvent;
import org.drools.event.AfterActivationFiredEvent;
import org.drools.event.AgendaEventListener;
import org.drools.event.AgendaGroupPoppedEvent;
import org.drools.event.AgendaGroupPushedEvent;
import org.drools.event.BeforeActivationFiredEvent;
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

    final Map<String, Integer>	firingCounts = new HashMap<String, Integer>(100);

	long totalFires;


    public TestingEventListener() {
    }

    public AgendaFilter getAgendaFilter(final HashSet<String> ruleNames, final boolean inclusive) {
    	return new AgendaFilter() {
			public boolean accept(Activation activation) {
				if (ruleNames.size() ==0) return true;
				String ruleName = activation.getRule().getName();
				
				http://www.wtf.com
					
				//jdelong: please don't want to see records of cancelled activations 
					
				if (inclusive) {
					if (ruleNames.contains(ruleName)) {
						return true;
					} else {
						//record(activation.getRule(), firingCounts);
						return false;
					}
					
				} else {
					if (!ruleNames.contains(ruleName)) {
						return true;
					} else {
						//record(activation.getRule(), firingCounts);
						return false;
					}
				}
			}
    	};
    }



//    /**
//     * Exclusive means DO NOT fire the rules mentioned.
//     * For those rules, they will still be counted, just not allowed to activate.
//     * Inclusive means only the rules on the given set are allowed to fire.
//     * The other rules will have their activation counted but not be allowed to fire.
//     */
//	static void stubOutRules(HashSet<String> ruleNames, RuleBase ruleBase,
//			boolean inclusive) {
//		if (ruleNames.size() > 0) {
//	    	if (inclusive) {
//	    		Package[] pkgs = ruleBase.getPackages();
//	    		for (int i = 0; i < pkgs.length; i++) {
//					Rule[] rules = pkgs[i].getRules();
//					for (int j = 0; j < rules.length; j++) {
//						Rule rule = rules[j];
//						if (!ruleNames.contains(rule.getName())) {
//							rule.setConsequence(new NilConsequence());
//						}
//					}
//				}
//	    	} else {
//	    		Package[] pkgs = ruleBase.getPackages();
//	    		for (int i = 0; i < pkgs.length; i++) {
//	    			Package pkg = pkgs[i];
//	    			for (Iterator iter = ruleNames.iterator(); iter.hasNext();) {
//						String name = (String) iter.next();
//						Rule rule = pkg.getRule(name);
//						rule.setConsequence(new NilConsequence());
//					}
//
//	    		}
//	    	}
//    	}
//	}





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

	private void record(Rule rule, Map<String, Integer> counts) {
		this.totalFires++;
		String name = rule.getName();
		if (!counts.containsKey(name)) {
			counts.put(name, 1);
		} else {
			counts.put(name, counts.get(name) + 1);
		}
	}



	/**
	 * @return A map of the number of times a given rule "fired".
	 * (of course in reality the side effect of its firing may have been nilled out).
	 */
	public Map<String, Integer> getFiringCounts() {
		return this.firingCounts;
	}

	/**
	 * Return a list of the rules fired, for display purposes.
	 */
	public String[] getRulesFiredSummary() {
		String[] r = new String[firingCounts.size()];
		int i = 0;
		for (Iterator iterator = firingCounts.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, Integer> e = (Entry<String, Integer>) iterator.next();
			r[i] = e.getKey() + " [" + e.getValue() + "]";
			i++;
		}

		return r;
	}



}

class NilConsequence implements Consequence {

	public void evaluate(KnowledgeHelper knowledgeHelper, WorkingMemory workingMemory) throws Exception {
	}
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }

    public void writeExternal(ObjectOutput out) throws IOException {

    }
    
    public String getName() {
        return "default";
    }    
}

