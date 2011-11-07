package org.jbpm.marshalling.util;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.drools.impl.EnvironmentFactory;
import org.drools.marshalling.impl.MarshallerReaderContext;
import org.drools.marshalling.impl.ProcessMarshaller;
import org.drools.runtime.Environment;
import org.drools.runtime.process.ProcessInstance;
import org.jbpm.marshalling.impl.ProcessMarshallerImpl;

public class MarshallingTestUtil {

    public static List<ProcessInstance> unmarshallProcessInstances(byte [] marshalledSessionByteArray) throws Exception { 
        // Setup env/context/stream
        Environment env = EnvironmentFactory.newEnvironment();
        ByteArrayInputStream bais = new ByteArrayInputStream(marshalledSessionByteArray);
        MarshallerReaderContext context = new MarshallerReaderContext(bais, null, null, null, env);

        // Unmarshall
        ProcessMarshaller processMarshaller = new ProcessMarshallerImpl();
        List<ProcessInstance> processInstanceList = null;
        try { 
            processInstanceList = processMarshaller.readProcessInstances(context);
        }
        catch( Exception e ) { 
            e.printStackTrace();
            throw e;
        }
        
        context.close();
        
        return processInstanceList;
    }
}
