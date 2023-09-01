package org.kie.internal.event.rule;

public interface RuleEventManager {

    void addEventListener( final RuleEventListener listener );

    void removeEventListener( final RuleEventListener listener );
}