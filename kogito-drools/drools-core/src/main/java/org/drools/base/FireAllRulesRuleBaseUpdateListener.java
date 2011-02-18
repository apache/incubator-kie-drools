/*
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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.StatefulSession;
import org.drools.event.BeforeRuleBaseUnlockedEvent;
import org.drools.event.DefaultRuleBaseEventListener;
import org.drools.spi.RuleBaseUpdateListener;

public class FireAllRulesRuleBaseUpdateListener extends DefaultRuleBaseEventListener
    implements
    RuleBaseUpdateListener,
    Externalizable {
    private StatefulSession session;

    public FireAllRulesRuleBaseUpdateListener() {
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        session = (StatefulSession)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(session);
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
