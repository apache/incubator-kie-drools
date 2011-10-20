package org.drools.integrationtests.marshalling;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.drools.integrationtests.marshalling.util.OldOutputMarshallerMethods;
import org.drools.marshalling.MarshallerFactory;
import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.marshalling.impl.InputMarshaller;
import org.drools.marshalling.impl.MarshallerProviderImpl;
import org.drools.marshalling.impl.MarshallerReaderContext;
import org.drools.marshalling.impl.MarshallerWriteContext;
import org.drools.marshalling.impl.ObjectMarshallingStrategyStore;
import org.drools.marshalling.impl.OutputMarshaller;
import org.drools.process.instance.WorkItem;
import org.drools.process.instance.impl.WorkItemImpl;
import org.junit.Test;

public class WorkItemMarshallingTest {

    private WorkItem createWorkItem(Object [] input ) { 
        int i = 0; 
        WorkItemImpl impl = new WorkItemImpl();
        impl.setId((Long) input[i++]);
        impl.setName((String) input[i++]);
        impl.setParameter((String) input[i++], input[i++] );
        impl.setParameter((String) input[i++], input[i++] );
        impl.setParameter((String) input[i++], input[i++] );
        
        impl.setProcessInstanceId((Long) input[i++]);
        impl.setState((Integer) input[i++]);
        
        return (WorkItem) impl;
    }

    private void checkWorkItem(WorkItem workItem, Object [] input) { 
        int i = 0;
        assertTrue( "id incorrect", input[i++].equals(workItem.getId()) );
        assertTrue( "name incorrect", input[i++].equals(workItem.getName()) );
        String paraName = (String) input[i++];
        assertTrue( "parameter " + paraName + " incorrect", input[i++].equals(workItem.getParameter(paraName)) );
        paraName = (String) input[i++];
        assertTrue( "parameter " + paraName + " incorrect", input[i++].equals(workItem.getParameter(paraName)) );
        paraName = (String) input[i++];
        Object paraVal = input[i++];
        assertTrue( "parameter " + paraName + " incorrect", 
                Arrays.equals((char []) paraVal, (char []) workItem.getParameter(paraName)) );
        assertTrue( "processInstanceId incorrect", input[i++].equals(workItem.getProcessInstanceId()) );
        assertTrue( "state incorrect", input[i++].equals(workItem.getState()) );
    }

    private char [] bandName = { 'a', 'c', 'd', 'c' };
    private Object [] input  = { 23l, 
                                "WorkItem", 
                                "para1", "meter", 
                                "para2", 141015,
                                "para3", bandName,
                                501l,
                                WorkItem.ACTIVE };

    @Test
    public void basicMarshallingTest() throws IOException { 
        WorkItem workItem = createWorkItem(input);
        
        ObjectMarshallingStrategy[] strats 
            = new ObjectMarshallingStrategy[] { MarshallerFactory.newSerializeMarshallingStrategy() };

        // marshall/serialize workItem
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MarshallerWriteContext outContext = new MarshallerWriteContext( baos, null, null, null, 
                new ObjectMarshallingStrategyStore(strats), true, true, null);
        OutputMarshaller.writeWorkItem(outContext, workItem);
        
        // unmarshall/deserialize workItem
        byte [] byteArray = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
        MarshallerReaderContext inContext = new MarshallerReaderContext( bais, null, null,
                new ObjectMarshallingStrategyStore(strats), true, true, null);
        workItem = InputMarshaller.readWorkItem(inContext);
       
        // Check
        checkWorkItem(workItem, input);
    }
    
    @Test
    public void multipleStrategyDifferentOrderMarshallingTest() throws IOException { 
        WorkItem workItem = createWorkItem(input);
       
        // marshall/serialize workItem
        byte [] byteArray;
        {
            ObjectMarshallingStrategy[] strats 
                = new ObjectMarshallingStrategy[] { 
                    MarshallerFactory.newSerializeMarshallingStrategy(), 
                    new MarshallerProviderImpl().newIdentityMarshallingStrategy() };
    
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MarshallerWriteContext outContext = new MarshallerWriteContext( baos, null, null, null, 
                    new ObjectMarshallingStrategyStore(strats), true, true, null);
            OutputMarshaller.writeWorkItem(outContext, workItem);
            byteArray = baos.toByteArray();
        }
       
        // unmarshall/deserialize workItem
        {
            // Reverse the order of strategies
            ObjectMarshallingStrategy[] newStrats 
                = new ObjectMarshallingStrategy[] { 
                    new MarshallerProviderImpl().newIdentityMarshallingStrategy(),
                    MarshallerFactory.newSerializeMarshallingStrategy()  };
    
            ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
            MarshallerReaderContext inContext = new MarshallerReaderContext( bais, null, null,
                new ObjectMarshallingStrategyStore(newStrats), true, true, null);
            workItem = InputMarshaller.readWorkItem(inContext);
        }
        
        // Check
        checkWorkItem(workItem, input);
    }

    @Test
    public void multipleStrategyOneLessMarshallingTest() throws IOException { 
        WorkItem workItem = createWorkItem(input);
    
        // marshall/serialize workItem
        byte [] byteArray;
        {
            ObjectMarshallingStrategy[] strats 
                = new ObjectMarshallingStrategy[] { 
                    MarshallerFactory.newSerializeMarshallingStrategy(), 
                    new MarshallerProviderImpl().newIdentityMarshallingStrategy() };
    
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MarshallerWriteContext outContext = new MarshallerWriteContext( baos, null, null, null, 
                    new ObjectMarshallingStrategyStore(strats), true, true, null);
            OutputMarshaller.writeWorkItem(outContext, workItem);
            byteArray = baos.toByteArray();
        }
    
        // unmarshall/deserialize workItem
        {
            // Only put serialization strategy in 
            ObjectMarshallingStrategy[] newStrats 
                = new ObjectMarshallingStrategy[] { 
                    MarshallerFactory.newSerializeMarshallingStrategy()  };
    
            ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
            MarshallerReaderContext inContext = new MarshallerReaderContext( bais, null, null,
                new ObjectMarshallingStrategyStore(newStrats), true, true, null);
            workItem = InputMarshaller.readWorkItem(inContext);
        }
        
        // Check
        checkWorkItem(workItem, input);
    }

    @Test
    public void multipleStrategyNoneLeftMarshallingTest() throws IOException { 
        WorkItem workItem = createWorkItem(input);
    
        // marshall/serialize workItem
        byte [] byteArray;
        {
            ObjectMarshallingStrategy[] strats 
                = new ObjectMarshallingStrategy[] { 
                    MarshallerFactory.newSerializeMarshallingStrategy(), 
                    new MarshallerProviderImpl().newIdentityMarshallingStrategy() };
    
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MarshallerWriteContext outContext = new MarshallerWriteContext( baos, null, null, null, 
                    new ObjectMarshallingStrategyStore(strats), true, true, null);
            OutputMarshaller.writeWorkItem(outContext, workItem);
            byteArray = baos.toByteArray();
        }
    
        // unmarshall/deserialize workItem
        {
            // Only put serialization strategy in 
            ObjectMarshallingStrategy[] newStrats 
                = new ObjectMarshallingStrategy[] { };
    
            ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
            MarshallerReaderContext inContext = new MarshallerReaderContext( bais, null, null,
                new ObjectMarshallingStrategyStore(newStrats), true, true, null);
           
            try { 
                workItem = InputMarshaller.readWorkItem(inContext);
                fail( "An exception was expected here." );
            }
            catch( IllegalStateException e ) { 
                assertTrue( e.getMessage() != null && e.getMessage().startsWith("No strategy of type") ) ;
            }
        }
    }
   
    @Test
    public void backwardsCompatibleWorkItemMarshalling() throws IOException { 
        WorkItem workItem = createWorkItem(input);
        
        // marshall/serialize workItem
        byte [] byteArray;
        {
            ObjectMarshallingStrategy[] strats 
                = new ObjectMarshallingStrategy[] { 
                    MarshallerFactory.newSerializeMarshallingStrategy(), 
                    new MarshallerProviderImpl().newIdentityMarshallingStrategy() };
    
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MarshallerWriteContext outContext = new MarshallerWriteContext( baos, null, null, null, 
                    new ObjectMarshallingStrategyStore(strats), true, true, null);
            OldOutputMarshallerMethods.writeWorkItem_v1(outContext, workItem);
            byteArray = baos.toByteArray();
        }
        
        // unmarshall/deserialize workItem
        {
            // Only put serialization strategy in 
            ObjectMarshallingStrategy[] newStrats 
                = new ObjectMarshallingStrategy[] { 
                    MarshallerFactory.newSerializeMarshallingStrategy()  };
    
            ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
            MarshallerReaderContext inContext = new MarshallerReaderContext( bais, null, null,
                new ObjectMarshallingStrategyStore(newStrats), true, true, null);
            workItem = InputMarshaller.readWorkItem(inContext);
        }
        
        // Check
        checkWorkItem(workItem, input);
    }
    
    
}
