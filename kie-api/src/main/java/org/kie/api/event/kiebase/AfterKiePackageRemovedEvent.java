package org.kie.api.event.kiebase;

import org.kie.api.definition.KiePackage;

public interface AfterKiePackageRemovedEvent
    extends
    KieBaseEvent {
    public KiePackage getKiePackage();
}
