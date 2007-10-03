/**
 * 
 */
package org.drools.spi;

import org.drools.StatefulSession;
import org.drools.event.RuleBaseEventListener;

public interface RuleBaseUpdateListener extends RuleBaseEventListener {
    public void setSession(StatefulSession session);
}