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

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStream;
import org.drools.ruleunits.api.RuleUnitData;

public class AlertingUnit implements RuleUnitData {

    private DataStream<Event> eventData = DataSource.createStream();
    private DataStream<Alert> alertData = DataSource.createStream();

    public DataStream<Event> getEventData() {
        return eventData;
    }

    public void setEventData(DataStream<Event> eventData) {
        this.eventData = eventData;
    }

    public DataStream<Alert> getAlertData() {
        return alertData;
    }

    public void setAlertData(DataStream<Alert> alertData) {
        this.alertData = alertData;
    }

}
