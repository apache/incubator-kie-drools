/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.kie.api.marshalling;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;

public interface Marshaller {

    /**
     * Marshalls the given KieSession into the provided OutputStream
     * @param stream
     * @param ksession
     * @throws IOException
     */
    void marshall(OutputStream stream,
                  KieSession ksession) throws IOException;
    
    /**
     * Creates KieSession using default KieSessionConfiguration and
     * Environment. It will then unmarshall the stream into the session. Either KieSessionConfiguration or
     * Environment may be null and it will use the default.
     * 
     * @param stream
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    KieSession unmarshall(InputStream stream) throws IOException, ClassNotFoundException;

    /**
     * Creates KieSession using the given KieSessionConfiguration and
     * Environment. It will then unmarshall the stream into the session. Either KieSessionConfiguration or
     * Environment may be null and it will use the default.
     * 
     * @param stream
     * @param config
     * @param environment
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    KieSession unmarshall(InputStream stream,
                          KieSessionConfiguration config,
                          Environment environment) throws IOException, ClassNotFoundException;

    /**
     * Unmarshall the stream into the KieSession. All existing state in the session will be lost.
     *  
     * @param stream
     * @param ksession
     * @throws IOException
     * @throws ClassNotFoundException
     */
    void unmarshall(InputStream stream,
                    KieSession ksession) throws IOException, ClassNotFoundException;

    /**
     * Returns the {@link org.kie.api.marshalling.MarshallingConfiguration} object for this marshaller.
     * 
     * @return
     */
    MarshallingConfiguration getMarshallingConfiguration();

}
