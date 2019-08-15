package org.drools.workbench.models.datamodel.rule;

import java.util.Objects;
import java.util.stream.Stream;

import org.kie.soup.project.datamodel.oracle.DataType;

public enum Attribute {
    SALIENCE("salience", DataType.TYPE_NUMERIC_INTEGER),
    ENABLED("enabled", DataType.TYPE_BOOLEAN),
    DATE_EFFECTIVE("date-effective", DataType.TYPE_DATE),
    DATE_EXPIRES("date-expires", DataType.TYPE_DATE),
    NO_LOOP("no-loop", DataType.TYPE_BOOLEAN),
    AGENDA_GROUP("agenda-group", DataType.TYPE_STRING),
    ACTIVATION_GROUP("activation-group", DataType.TYPE_STRING),
    DURATION("duration", DataType.TYPE_NUMERIC_LONG),
    TIMER("timer", DataType.TYPE_STRING),
    CALENDARS("calendars", DataType.TYPE_STRING),
    AUTO_FOCUS("auto-focus", DataType.TYPE_BOOLEAN),
    LOCK_ON_ACTIVE("lock-on-active", DataType.TYPE_BOOLEAN),
    RULEFLOW_GROUP("ruleflow-group", DataType.TYPE_STRING),
    DIALECT("dialect", DataType.TYPE_STRING),
    NEGATE_RULE("negate", DataType.TYPE_BOOLEAN);

    private final String attributeName;

    private final String dataType;

    Attribute(final String attributeName, final String dataType) {
        this.attributeName = attributeName;
        this.dataType = dataType;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public static String getAttributeDataType(final String name) {
        return Stream.of(Attribute.values())
                .filter(a -> Objects.equals(a.getAttributeName(), name))
                .findFirst()
                .map(a -> a.dataType)
                .orElse(DataType.TYPE_STRING);
    }

}
