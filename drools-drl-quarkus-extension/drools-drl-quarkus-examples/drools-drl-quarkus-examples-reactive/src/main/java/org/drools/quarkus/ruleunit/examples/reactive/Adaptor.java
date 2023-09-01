package org.drools.quarkus.ruleunit.examples.reactive;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.drools.ruleunits.api.DataObserver;
import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitInstance;

import io.quarkus.runtime.Startup;

@Startup
@ApplicationScoped
public class Adaptor {

    @Inject
    RuleUnit<AlertingUnit> ruleUnit;

    AlertingUnit alertingUnit;
    RuleUnitInstance<AlertingUnit> ruleUnitInstance;

    @Inject
    @Channel("alerts")
    Emitter<Alert> emitter;

    @PostConstruct
    void init() {
        this.alertingUnit = new AlertingUnit();
        this.ruleUnitInstance = ruleUnit.createInstance(alertingUnit);
        alertingUnit.getAlertData().subscribe(DataObserver.of(emitter::send));
    }

    @Incoming("events")
    public void receive(Event event) throws InterruptedException {
        alertingUnit.getEventData().append(event);
        ruleUnitInstance.fire();
    }
}
