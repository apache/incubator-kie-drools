/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
