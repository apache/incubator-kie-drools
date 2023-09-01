package org.drools.base.time.impl;

import java.io.Serializable;

import org.drools.base.base.ValueResolver;
import org.drools.base.rule.RuleComponent;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.RuleConditionElement;
import org.drools.base.time.JobHandle;
import org.drools.base.time.Trigger;
import org.kie.api.runtime.Calendars;

public interface Timer extends Serializable, RuleComponent, RuleConditionElement {

    Trigger createTrigger( long timestamp, String[] calendarNames, Calendars calendars);

    Trigger createTrigger(long timestamp,
                          BaseTuple leftTuple,
                          JobHandle jh,
                          String[] calendarNames,
                          Calendars calendars,
                          Declaration[][] declrs,
                          ValueResolver valueResolver);
}
