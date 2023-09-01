package org.kie.api.event.kiebase;

import org.kie.api.definition.KiePackage;


public interface AfterKiePackageAddedEvent
    extends
    KieBaseEvent {

    public KiePackage getKiePackage();
}
