/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * 
 */
package org.drools.base;

import org.drools.StatefulSession;
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