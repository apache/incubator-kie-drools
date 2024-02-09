/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.decisiontable.parser;

import java.util.HashMap;
import java.util.Map;

import org.drools.decisiontable.parser.ActionType.Code;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ActionTypeTest {

    @Test
    public void testChooseActionType() {

        checkActionType("C", Code.CONDITION);
        checkActionType("CONDITION", Code.CONDITION);
        
        checkActionType("A", Code.ACTION);
        checkActionType("ACTION", Code.ACTION);

        checkActionType("N", Code.NAME);
        checkActionType("NAME", Code.NAME);

        checkActionType("I", Code.DESCRIPTION);
        checkActionType("DESCRIPTION", Code.DESCRIPTION);
        
        checkActionType("P", Code.SALIENCE);
        checkActionType("PRIORITY", Code.SALIENCE);

        checkActionType("D", Code.DURATION);
        checkActionType("DURATION", Code.DURATION);

        checkActionType("T", Code.TIMER);
        checkActionType("TIMER", Code.TIMER);

        checkActionType("E", Code.CALENDARS);
        checkActionType("CALENDARS", Code.CALENDARS);

        checkActionType("U", Code.NOLOOP);
        checkActionType("NO-LOOP", Code.NOLOOP);

        checkActionType("L", Code.LOCKONACTIVE);
        checkActionType("LOCK-ON-ACTIVE", Code.LOCKONACTIVE);

        checkActionType("F", Code.AUTOFOCUS);
        checkActionType("AUTO-FOCUS", Code.AUTOFOCUS);

        checkActionType("X", Code.ACTIVATIONGROUP);
        checkActionType("ACTIVATION-GROUP", Code.ACTIVATIONGROUP);

        checkActionType("G", Code.AGENDAGROUP);
        checkActionType("AGENDA-GROUP", Code.AGENDAGROUP);

        checkActionType("R", Code.RULEFLOWGROUP);
        checkActionType("RULEFLOW-GROUP", Code.RULEFLOWGROUP);

        checkActionType("V", Code.DATEEFFECTIVE);
        checkActionType("DATE-EFFECTIVE", Code.DATEEFFECTIVE);

        checkActionType("Z", Code.DATEEXPIRES);
        checkActionType("DATE-EXPIRES", Code.DATEEXPIRES);

        checkActionType("@", Code.METADATA);
        checkActionType("METADATA", Code.METADATA);

    }
    
    private void checkActionType(String value, Code code) {
        Map<Integer, ActionType> actionTypeMap = new HashMap<>();
        ActionType.addNewActionType(actionTypeMap, value, 0, 1);

        assertThat(actionTypeMap.get(0).getCode()).isEqualTo(code);
    }
    
}
