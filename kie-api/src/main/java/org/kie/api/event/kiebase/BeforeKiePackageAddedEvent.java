package org.kie.api.event.kiebase;

import org.kie.api.definition.KiePackage;


public interface BeforeKiePackageAddedEvent
    extends
    KieBaseEvent {
    KiePackage getKiePackage();
}
