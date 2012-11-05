package org.drools.integrationtests.activation;

import java.util.HashMap;
import java.util.Map;

import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.event.rule.DefaultAgendaEventListener;

/**
 * AgendaEventListener to track fired rules.
 * When rule is fired for the first time it's added to fired rules and
 * when the rule fires afterwards the counter is incremented to make it
 * possible to track how many times the rule was fired
 *
 */
public class TrackingAgendaEventListener extends DefaultAgendaEventListener {

    private Map<String, Integer> rulesFired = new HashMap<String, Integer>();

    @Override
    public void afterActivationFired(AfterActivationFiredEvent event) {
        super.afterActivationFired(event);

        String rule = event.getActivation().getRule().getName();
        if (isRuleFired(rule)) {
            rulesFired.put(rule, rulesFired.get(rule) + 1);
        } else {
            rulesFired.put(rule, 1);
        }
    }

    /**
     * Return true if the rule was fired at least once
     *
     * @param rule - name of the rule
     * @return true if the rule was fired
     */
    public boolean isRuleFired(String rule) {
        return rulesFired.containsKey(rule);
    }

    /**
     * Returns number saying how many times the rule was fired
     *
     * @param rule - name of the rule
     * @return number how many times rule was fired, 0 if rule wasn't fired
     */
    public int ruleFiredCount(String rule) {
        if (isRuleFired(rule)) {
            return rulesFired.get(rule);
        } else {
            return 0;
        }
    }

    /**
     * @return how many rules were fired
     */
    public int rulesCount() {
        return rulesFired.size();
    }

    /**
     * Clears all the information
     */
    public void clear() {
        rulesFired.clear();
    }
}
