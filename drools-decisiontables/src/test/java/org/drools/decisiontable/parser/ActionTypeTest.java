package org.drools.decisiontable.parser;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.decisiontable.parser.ActionType;

public class ActionTypeTest extends TestCase {

    public void testChooseActionType() {
        Map actionTypeMap = new HashMap();
        ActionType.addNewActionType( actionTypeMap, "C", 0, 1 );
        
        ActionType type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(ActionType.CONDITION, type.type);
        
        
        actionTypeMap = new HashMap();
        ActionType.addNewActionType( actionTypeMap, "A", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(ActionType.ACTION, type.type);
        
        actionTypeMap = new HashMap();
        ActionType.addNewActionType( actionTypeMap, "X", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(ActionType.ACTIVATIONGROUP, type.type);
        
        actionTypeMap = new HashMap();
        ActionType.addNewActionType( actionTypeMap, "ACTIVATION-GROUP", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(ActionType.ACTIVATIONGROUP, type.type);
        
        actionTypeMap = new HashMap();
        ActionType.addNewActionType( actionTypeMap, "NO-LOOP", 0, 1 );
        type = (ActionType) actionTypeMap.get( new Integer(0) );
        assertEquals(ActionType.NOLOOP, type.type);

        
        
        
    }
    
}
