package org.jbpm.task.event;

import static junit.framework.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.TreeMap;

import org.jbpm.task.event.entity.TaskEvent;
import org.jbpm.task.event.entity.TaskEventFactory;
import org.jbpm.task.event.entity.TaskEventType;
import org.junit.Test;

public class TaskEventTest {

    private final boolean print = false;
    
    @Test
    public void testReadWriteExternal() throws Exception { 
        long taskId = 23;
        String userId = "illuminatus";
        TaskEvent event = TaskEventFactory.createClaimedEvent(taskId, userId, -1);
        
        // Serialise the object
        ByteArrayOutputStream baos = new ByteArrayOutputStream(100);
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(event);
        oos.close();

        // Deserialize the object in serialized form
        final byte[] serializedEvent = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(serializedEvent);
        ObjectInputStream ois = new ObjectInputStream(bais);
        TaskEvent deserializedEvent = (TaskEvent) ois.readObject();
        ois.close();
        
        checkEvent(event, deserializedEvent);
    }
    
    private void checkEvent(TaskEvent orig, TaskEvent copy) { 
        assertTrue("event time doesn't match.", orig.getEventTime().getTime() == copy.getEventTime().getTime() );
        assertTrue("id doesn't match.", orig.getId() == copy.getId() || orig.getId().equals(copy.getId()));
        assertTrue("event type doesn't match", orig.getType() == copy.getType() || orig.getType().equals(copy.getType()));
        assertTrue("task id doesn't match", orig.getTaskId() == copy.getTaskId());
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
