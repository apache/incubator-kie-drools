package org.drools.marshalling;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.drools.runtime.Environment;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;

public interface Marshaller {

    /**
     * Marshalls the given StatefulKnowledgeSession into the provided OutputStream
     * @param stream
     * @param session
     * @throws IOException
     */
    void marshall(OutputStream stream,
                  StatefulKnowledgeSession ksession) throws IOException;
    
    /**
     * Creates StatefulKnowledgeSession using default KnowledgeSessionConfiguration and 
     * Environment. It will then unmarshall the stream into the session. Either KnowledgeSessionConfiguration or
     * Environment may be null and it will use the default.
     * 
     * @param stream
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public StatefulKnowledgeSession unmarshall(InputStream stream) throws IOException,
                                                                      ClassNotFoundException;   

    /**
     * Creates StatefulKnowledgeSession using the given KnowledgeSessionConfiguration and 
     * Environment. It will then unmarshall the stream into the session. Either KnowledgeSessionConfiguration or
     * Environment may be null and it will use the default.
     * 
     * @param stream
     * @param config
     * @param environment
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public StatefulKnowledgeSession unmarshall(InputStream stream,
                                               KnowledgeSessionConfiguration config,
                                               Environment environment) throws IOException,
                                                                      ClassNotFoundException;

    /**
     * Unmarshall the stream into the StatefulKnowledgeSession. All existing state in the session will be lost.
     *  
     * @param stream
     * @param session
     * @throws IOException
     * @throws ClassNotFoundException
     */
    void unmarshall(InputStream stream,
                   StatefulKnowledgeSession ksession) throws IOException,
                                                    ClassNotFoundException;

}