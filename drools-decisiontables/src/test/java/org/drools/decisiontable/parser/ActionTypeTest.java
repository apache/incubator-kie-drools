/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
                
        Map<Integer, ActionType> actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "C", 0, 1 );
        ActionType type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertThat(type.getCode()).isEqualTo(Code.CONDITION);

        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "CONDITION", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertThat(type.getCode()).isEqualTo(Code.CONDITION);
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "A", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertThat(type.getCode()).isEqualTo(Code.ACTION);
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "ACTION", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertThat(type.getCode()).isEqualTo(Code.ACTION);

        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "N", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertThat(type.getCode()).isEqualTo(Code.NAME);
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "NAME", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertThat(type.getCode()).isEqualTo(Code.NAME);

        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "I", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertThat(type.getCode()).isEqualTo(Code.DESCRIPTION);
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "DESCRIPTION", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertThat(type.getCode()).isEqualTo(Code.DESCRIPTION);
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "P", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertThat(type.getCode()).isEqualTo(Code.SALIENCE);
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "PRIORITY", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertThat(type.getCode()).isEqualTo(Code.SALIENCE);

        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "D", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertThat(type.getCode()).isEqualTo(Code.DURATION);
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "DURATION", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertThat(type.getCode()).isEqualTo(Code.DURATION);

        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "T", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertThat(type.getCode()).isEqualTo(Code.TIMER);
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "TIMER", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertThat(type.getCode()).isEqualTo(Code.TIMER);

        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "E", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertThat(type.getCode()).isEqualTo(Code.CALENDARS);
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "CALENDARS", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertThat(type.getCode()).isEqualTo(Code.CALENDARS);
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "U", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertThat(type.getCode()).isEqualTo(Code.NOLOOP);
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "NO-LOOP", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertThat(type.getCode()).isEqualTo(Code.NOLOOP);
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "L", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertThat(type.getCode()).isEqualTo(Code.LOCKONACTIVE);
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "LOCK-ON-ACTIVE", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertThat(type.getCode()).isEqualTo(Code.LOCKONACTIVE);
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "F", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertThat(type.getCode()).isEqualTo(Code.AUTOFOCUS);
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "AUTO-FOCUS", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertThat(type.getCode()).isEqualTo(Code.AUTOFOCUS);

        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "X", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertThat(type.getCode()).isEqualTo(Code.ACTIVATIONGROUP);
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "ACTIVATION-GROUP", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertThat(type.getCode()).isEqualTo(Code.ACTIVATIONGROUP);

        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "G", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertThat(type.getCode()).isEqualTo(Code.AGENDAGROUP);
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "AGENDA-GROUP", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertThat(type.getCode()).isEqualTo(Code.AGENDAGROUP);
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "R", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertThat(type.getCode()).isEqualTo(Code.RULEFLOWGROUP);

        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "RULEFLOW-GROUP", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertThat(type.getCode()).isEqualTo(Code.RULEFLOWGROUP);

        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "V", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertThat(type.getCode()).isEqualTo(Code.DATEEFFECTIVE);

        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "DATE-EFFECTIVE", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertThat(type.getCode()).isEqualTo(Code.DATEEFFECTIVE);

        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "Z", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertThat(type.getCode()).isEqualTo(Code.DATEEXPIRES);

        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "DATE-EXPIRES", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertThat(type.getCode()).isEqualTo(Code.DATEEXPIRES);

        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "@", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertThat(type.getCode()).isEqualTo(Code.METADATA);

        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "METADATA", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertThat(type.getCode()).isEqualTo(Code.METADATA);

    }
    
}
