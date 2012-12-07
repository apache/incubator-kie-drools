/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.marshalling;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.kie.runtime.Environment;
import org.kie.runtime.KieSession;
import org.kie.runtime.KnowledgeSessionConfiguration;

public interface Marshaller {

    /**
     * Marshalls the given StatefulKnowledgeSession into the provided OutputStream
     * @param stream
     * @param session
     * @throws IOException
     */
    void marshall(OutputStream stream,
                  KieSession ksession) throws IOException;
    
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
    public KieSession unmarshall(InputStream stream) throws IOException,
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
    public KieSession unmarshall(InputStream stream,
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
                    KieSession ksession) throws IOException,
                                                    ClassNotFoundException;

    /**
     * Returns the MarshallingConfiguration object for this marshaller
     * 
     * @return
     */
    public MarshallingConfiguration getMarshallingConfiguration();

}
