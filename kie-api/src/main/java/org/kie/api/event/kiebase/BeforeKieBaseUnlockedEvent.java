package org.kie.api.event.kiebase;

import org.kie.api.KieBase;


public interface BeforeKieBaseUnlockedEvent
    extends
    KieBaseEvent {
    KieBase getKieBase();
}
