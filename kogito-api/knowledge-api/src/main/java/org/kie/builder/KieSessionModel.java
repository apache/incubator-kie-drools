package org.kie.builder;

import org.kie.runtime.conf.ClockTypeOption;

public interface KieSessionModel {

    String getName();

    KieSessionModel setName(String name);

    String getType();

    KieSessionModel setType(String type);

    ClockTypeOption getClockType();

    KieSessionModel setClockType(ClockTypeOption clockType);
}