/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.model;

import java.util.Calendar;
import java.util.Map;

public interface Rule extends NamedModelItem {

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
    Map<String, Object> getMetaData();
    Object getMetaData(String name);

    String getUnit();

}
