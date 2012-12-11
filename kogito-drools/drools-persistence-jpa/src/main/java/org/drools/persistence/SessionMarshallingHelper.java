package org.drools.persistence;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.kie.KnowledgeBase;
import org.kie.marshalling.Marshaller;
import org.kie.marshalling.MarshallerFactory;
import org.kie.marshalling.ObjectMarshallingStrategy;
import org.kie.runtime.Environment;
import org.kie.runtime.EnvironmentName;
import org.kie.runtime.KieSession;
import org.kie.runtime.KieSessionConfiguration;
import org.kie.runtime.StatefulKnowledgeSession;

public class SessionMarshallingHelper {

    private KnowledgeBase                 kbase;
    private KieSessionConfiguration conf;
    private KieSession      			  ksession;
    private Marshaller                    marshaller;
    private Environment                   env;

    /**
     * Exist Info, so load session from here
     * @param info
     * @param ruleBase
     * @param conf
     * @param marshallingConfiguration
     */
    public SessionMarshallingHelper(KnowledgeBase kbase,
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
     * @param info
     * @param session
     * @param conf
     * @param marshallingConfiguration
     */
    public SessionMarshallingHelper(StatefulKnowledgeSession ksession,
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

    public KnowledgeBase getKbase() {
        return kbase;
    }

    public KieSessionConfiguration getConf() {
        return conf;
    }
    
    public Marshaller getMarshaller() {
    	return marshaller;
    }

    
}
