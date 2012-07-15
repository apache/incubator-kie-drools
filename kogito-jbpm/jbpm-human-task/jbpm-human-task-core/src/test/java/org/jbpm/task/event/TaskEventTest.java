package org.jbpm.task.event;

import static junit.framework.Assert.*;

import java.util.HashSet;
import java.util.TreeMap;

import org.drools.command.assertion.AssertEquals;
import org.jbpm.task.event.entity.TaskEvent;
import org.jbpm.task.event.entity.TaskEventType;
import org.junit.Test;

public class TaskEventTest {

   
    @Test
    public void testReadWriteExternal() { 
    }
    
    @Test
    public void testWriteReadExternal() { 
        
    }
    
    @Test
    public void verifyUniqueValues() {
        TaskEventType[] types = TaskEventType.values();

        HashSet<String> typeStrings = new HashSet<String>();
        for (int i = 0; i < types.length; ++i) {
            assertTrue("Type string " + types[i].getValue() + " has already been used.", typeStrings.add(types[i].getValue()));
        }
    }

    @Test
    public void createSwitchStatement() {
        boolean print = true;
        TaskEventType[] types = TaskEventType.values();
        TreeMap<String, TaskEventType> typeStrings = new TreeMap<String, TaskEventType>();
        for (int i = 0; i < types.length; ++i) {
            typeStrings.put(types[i].toString(), types[i]);
        }
        if(print) { 
            System.out.println("int hashCode = type.hashCode();"); 
            System.out.println("switch(hashCode) {"); 
        }
        HashSet<Integer> uniqueHashCode = new HashSet<Integer>();
        for( String type : typeStrings.keySet() ) { 
           int hashCode = typeStrings.get(type).getValue().hashCode();
           assertTrue( "HashCode is not unique: " + hashCode, uniqueHashCode.add(hashCode) );
           if(print) { 
               System.out.println("  case " + hashCode + ":"); 
               System.out.println("      return " + TaskEventType.class.getSimpleName() + "." + typeStrings.get(type) + ";"); 
           }
        }
        if(print) { 
            System.out.println("  default:");
            System.out.println("    throw new IllegalStateException(\"Unknown type: \" + type );");
            System.out.println("}");
        }
    }
    
    @Test
    public void testSwitchStatement() { 
        TaskEventType[] types = TaskEventType.values();
        for( int i = 0; i < types.length; ++i ) { 
            assertEquals("test failed for " + types[i].toString(), types[i], TaskEventType.getTypeFromValue(types[i].getValue()) ); 
        }
    }
    
   

}
