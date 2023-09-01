package org.kie.api.event.kiebase;

import org.kie.api.definition.KiePackage;


public interface BeforeKiePackageRemovedEvent
    extends
    KieBaseEvent {
    KiePackage getKiePackage();
}
