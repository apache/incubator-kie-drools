/**
 * 
 */
package org.drools.base;

import org.drools.StatefulSession;
import org.drools.common.InternalWorkingMemory;
import org.drools.event.DefaultRuleBaseEventListener;
import org.drools.event.knowledgebase.BeforeKnowledgeBaseUnlockedEvent;
import org.drools.spi.RuleBaseUpdateListener;

public class AsyncFireAllRulesRuleBaseUpdateListener extends DefaultRuleBaseEventListener 
implements RuleBaseUpdateListener {
    private StatefulSession session;
    
    public AsyncFireAllRulesRuleBaseUpdateListener() {
        
    }
    
    public void setSession(StatefulSession session) {
        this.session = (StatefulSession) session;
    }
    
    public void beforeRuleBaseUnlocked(BeforeKnowledgeBaseUnlockedEvent event) {
        if ( session.getRuleBase().getAdditionsSinceLock() > 0 ) { 
            session.asyncFireAllRules();
        }
    }
}