package org.drools.model;

import java.util.Calendar;
import java.util.Map;

public interface Rule {

    interface Attribute<T> {
        T getDefaultValue();

        Attribute<Boolean> NO_LOOP = () -> false;
        Attribute<Boolean> LOCK_ON_ACTIVE = () -> false;
        Attribute<Boolean> ENABLED = () -> true;
        Attribute<Boolean> AUTO_FOCUS = () -> false;
        Attribute<Integer> SALIENCE = () -> 0;
        Attribute<String> AGENDA_GROUP = () -> "MAIN";
        Attribute<String> ACTIVATION_GROUP = () -> null;
        Attribute<String> RULEFLOW_GROUP = () -> null;
        Attribute<String> DURATION = () -> null;
        Attribute<String> TIMER = () -> null;
        Attribute<String[]> CALENDARS = () -> new String[0];
        Attribute<Calendar> DATE_EFFECTIVE = () -> null;
        Attribute<Calendar> DATE_EXPIRES = () -> null;
    }


    View getView();

    Consequence getDefaultConsequence();
    Map<String, Consequence> getConsequences();

    <T> T getAttribute(Attribute<T> attribute);

    String getName();
    String getPackage();
    String getUnit();
}
