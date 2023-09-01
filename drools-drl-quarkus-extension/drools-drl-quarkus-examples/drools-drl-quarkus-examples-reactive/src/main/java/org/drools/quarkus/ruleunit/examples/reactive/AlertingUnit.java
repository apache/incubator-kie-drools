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
