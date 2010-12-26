package org.drools.decisiontable.parser;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.decisiontable.parser.ActionType;

public class ActionTypeTest {

    @Test
    public void testChooseActionType() {
        Map<Integer, ActionType> actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "C", 0, 1 );
        
        ActionType type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(ActionType.CONDITION, type.type);
        
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "A", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(ActionType.ACTION, type.type);
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "X", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(ActionType.ACTIVATIONGROUP, type.type);
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "ACTIVATION-GROUP", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(ActionType.ACTIVATIONGROUP, type.type);
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "NO-LOOP", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(ActionType.NOLOOP, type.type);
        
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType( actionTypeMap, "RULEFLOW-GROUP", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(ActionType.RULEFLOWGROUP, type.type);
    }
    
}
