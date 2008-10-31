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