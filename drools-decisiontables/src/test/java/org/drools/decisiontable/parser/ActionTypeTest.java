package org.drools.decisiontable.parser;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.decisiontable.parser.ActionType;
import static org.drools.decisiontable.parser.ActionType.Code;

public class ActionTypeTest {

    @Test
    public void testChooseActionType() {
                
        Map<Integer, ActionType> actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "C", 0, 1 );
        ActionType type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals( Code.CONDITION, type.getCode() );

        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "CONDITION", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(Code.CONDITION, type.getCode());
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "A", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(Code.ACTION, type.getCode());
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "ACTION", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(Code.ACTION, type.getCode());

        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "N", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(Code.NAME, type.getCode());
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "NAME", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(Code.NAME, type.getCode());

        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "I", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(Code.DESCRIPTION, type.getCode());
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "DESCRIPTION", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(Code.DESCRIPTION, type.getCode());
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "P", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(Code.SALIENCE, type.getCode());
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "PRIORITY", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(Code.SALIENCE, type.getCode());

        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "D", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(Code.DURATION, type.getCode());
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "DURATION", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(Code.DURATION, type.getCode());

        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "T", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(Code.TIMER, type.getCode());
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "TIMER", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(Code.TIMER, type.getCode());

        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "E", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(Code.CALENDARS, type.getCode());
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "CALENDARS", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(Code.CALENDARS, type.getCode());
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "U", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(Code.NOLOOP, type.getCode());
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "NO-LOOP", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(Code.NOLOOP, type.getCode());
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "L", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(Code.LOCKONACTIVE, type.getCode());
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "LOCK-ON-ACTIVE", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(Code.LOCKONACTIVE, type.getCode());
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "F", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(Code.AUTOFOCUS, type.getCode());
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "AUTO-FOCUS", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(Code.AUTOFOCUS, type.getCode());

        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "X", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(Code.ACTIVATIONGROUP, type.getCode());
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "ACTIVATION-GROUP", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(Code.ACTIVATIONGROUP, type.getCode());

        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "G", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(Code.AGENDAGROUP, type.getCode());
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "AGENDA-GROUP", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(Code.AGENDAGROUP, type.getCode());
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "R", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(Code.RULEFLOWGROUP, type.getCode());

        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "RULEFLOW-GROUP", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(Code.RULEFLOWGROUP, type.getCode());

        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "@", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(Code.METADATA, type.getCode());

        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "METADATA", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(Code.METADATA, type.getCode());

    }
    
}
