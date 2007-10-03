/**
 * 
 */
package org.drools.base;

import org.drools.StatefulSession;
import org.drools.event.BeforeRuleBaseUnlockedEvent;
import org.drools.event.DefaultRuleBaseEventListener;
import org.drools.spi.RuleBaseUpdateListener;

public class FireAllRulesRuleBaseUpdateListener extends DefaultRuleBaseEventListener 
implements RuleBaseUpdateListener {
    private StatefulSession session;
    
    public FireAllRulesRuleBaseUpdateListener() {
        
    }
    
    public void setSession(StatefulSession session) {
        this.session = session;
    }
    
    public void beforeRuleBaseUnlocked(BeforeRuleBaseUnlockedEvent event) {
        if ( session.getRuleBase().getAdditionsSinceLock() > 0 ) {
            session.fireAllRules();
        }
    }
}