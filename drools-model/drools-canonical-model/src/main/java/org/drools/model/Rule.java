package org.drools.model;

import java.util.Map;

public interface Rule {

    interface Attribute<T> {
        T getDefaultValue();

        Attribute<Boolean> NO_LOOP = () -> false;
        Attribute<Boolean> LOCK_ON_ACTIVE = () -> false;
        Attribute<Boolean> ENABLED = () -> true;
        Attribute<Integer> SALIENCE = () -> 0;
        Attribute<String> AGENDA_GROUP = () -> "MAIN";
        Attribute<String> ACTIVATION_GROUP = () -> null;
        Attribute<String> RULEFLOW_GROUP = () -> null;
        Attribute<String> DURATION = () -> null;
        Attribute<String> TIMER = () -> null;
        Attribute<String> CALENDAR = () -> null;
    }


    View getView();

    Consequence getDefaultConsequence();
    Map<String, Consequence> getConsequences();

    <T> T getAttribute(Attribute<T> attribute);

    String getName();
    String getPackage();
    String getUnit();
}
