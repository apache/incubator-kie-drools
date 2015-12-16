/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.persistence;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.kie.api.KieBase;
import org.kie.api.marshalling.Marshaller;
import org.kie.internal.marshalling.MarshallerFactory;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;

public class SessionMarshallingHelper {

    private KieBase kbase;
    private KieSessionConfiguration       conf;
    private KieSession      			  ksession;
    private Marshaller                    marshaller;
    private Environment                   env;

    /**
     * Exist Info, so load session from here
     */
    public SessionMarshallingHelper( KieBase kbase,
                                     KieSessionConfiguration conf,
                                     Environment env) {
        this.kbase = kbase;
        this.conf = conf;
        this.env = env;
        ObjectMarshallingStrategy[] strategies = (ObjectMarshallingStrategy[]) env.get( EnvironmentName.OBJECT_MARSHALLING_STRATEGIES );
        if (strategies  != null ) {
            // use strategies if provided in the environment
            this.marshaller = MarshallerFactory.newMarshaller( kbase, strategies );
        } else {
            this.marshaller = MarshallerFactory.newMarshaller( kbase ) ;
        }
    }

    /** 
     * new session, don't write now as info will request it on update callback
     */
    public SessionMarshallingHelper( KieSession ksession,
                                     KieSessionConfiguration conf) {
        this.ksession = ksession;
        this.kbase = ksession.getKieBase();
        this.conf = conf;
        this.env = ksession.getEnvironment();
        ObjectMarshallingStrategy[] strategies = (ObjectMarshallingStrategy[]) this.env.get( EnvironmentName.OBJECT_MARSHALLING_STRATEGIES );
        if (strategies  != null ) {
            // use strategies if provided in the environment
            this.marshaller = MarshallerFactory.newMarshaller( kbase, strategies );
        } else {
            this.marshaller = MarshallerFactory.newMarshaller( kbase ) ;
        }
        
    }

    public byte[] getSnapshot() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            marshaller.marshall( baos,
                                 ksession );
        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to get session snapshot",
                                        e );
        }

        return baos.toByteArray();
    }

    public KieSession loadSnapshot(byte[] bytes,
                                   KieSession ksession) {
        this.ksession = ksession;
        ByteArrayInputStream bais = new ByteArrayInputStream( bytes );
        try {
            if ( this.ksession != null ) {
                this.marshaller.unmarshall( bais,
                                            this.ksession );
            } else {
                this.ksession = this.marshaller.unmarshall( bais,
                                                            this.conf,
                                                            this.env );
            }
        } catch ( Exception e ) {
            throw new RuntimeException( "Unable to load session snapshot",
                                        e );
        }
        return this.ksession;
    }


    public KieSession getObject() {
        return ksession;
    }

    public KieBase getKbase() {
        return kbase;
    }

    public KieSessionConfiguration getConf() {
        return conf;
    }
    
    public Marshaller getMarshaller() {
    	return marshaller;
    }

    
}
