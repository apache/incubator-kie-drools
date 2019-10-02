/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.drools.workbench.models.datamodel.rule;

import java.util.Objects;
import java.util.stream.Stream;

import org.kie.soup.project.datamodel.oracle.DataType;

/**
 * This enum collects all attributes supported by kie tooling - guided rule editors, guided decision table editor ...
 * The enum stores pairs (attribute name, attribute data type)
 * The 'attribute data type' determines what HTML element will be shown for user to set the attribute value
 * e.g. TYPE_BOOLEAN -> checkbox, TYPE_DATE -> date picker ...
 */
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
