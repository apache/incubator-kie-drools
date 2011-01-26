package org.drools.persistence;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.common.InternalKnowledgeRuntime;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.marshalling.Marshaller;
import org.drools.marshalling.MarshallerFactory;
import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.runtime.Environment;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MarshallerBasedApplicationScopedPersistenceContext
    implements
        ApplicationScopedPersistenceContext {

    private Marshaller    statefulKnowledgeMarshaller;
    private static Logger logger = LoggerFactory
                                         .getLogger( MarshallerBasedApplicationScopedPersistenceContext.class );

    public MarshallerBasedApplicationScopedPersistenceContext(KnowledgeBase kbase,
                                                              List<ObjectMarshallingStrategy> marshallingStrategies) {
        createKnowledgeSessionMarshaller( kbase,
                                          marshallingStrategies );
    }

    public final StatefulKnowledgeSession loadStatefulKnowledgeSession(long sessionId,
                                                                 KnowledgeSessionConfiguration kconf,
                                                                 Environment env) {
        byte[] ksessionSnapshot = internalLoadStatefulKnowledgeSession( sessionId );
        try {
            StatefulKnowledgeSession ksession = statefulKnowledgeMarshaller
                    .unmarshall(
                                 new ByteArrayInputStream( ksessionSnapshot ),
                                 kconf,
                                 env );
            ((InternalKnowledgeRuntime) ksession).setId( sessionId );
            logger.debug( "loaded: KnowledgeSession {}",
                          ksession.getId() );
            return ksession;
        } catch ( Exception e ) {
            throw new IllegalStateException(
                                             "unable to unmarshall knowledge session",
                                             e );
        }
    }

    public final void save(StatefulKnowledgeSession ksession) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            statefulKnowledgeMarshaller.marshall( baos,
                                                  ksession );
        } catch ( IOException e ) {
            throw new IllegalStateException(
                                             "unable to marshall knowledge session",
                                             e );
        }

        int ksessionId = internalSaveStatefulKnowledgeSession( baos.toByteArray() );
        ((StatefulKnowledgeSessionImpl) ksession).setId( ksessionId );

    }

    public final void update(StatefulKnowledgeSession ksession) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            statefulKnowledgeMarshaller.marshall( baos,
                                                  ksession );
        } catch ( IOException e ) {
            throw new IllegalStateException(
                                             "unable to marshall knowledge session",
                                             e );
        }

        internalUpdateStatefulKnowledgeSession( ksession.getId(),
                        baos.toByteArray() );

    }

    private void createKnowledgeSessionMarshaller(KnowledgeBase kbase,
                                                  List<ObjectMarshallingStrategy> marshallingStrategies) {
        List<ObjectMarshallingStrategy> strategies = marshallingStrategies;
        if ( strategies != null ) {
            statefulKnowledgeMarshaller = MarshallerFactory.newMarshaller(
                                                                           kbase,
                                                                           strategies
                                                                                   .toArray( new ObjectMarshallingStrategy[strategies
                                                                                           .size()] ) );
        } else {
            statefulKnowledgeMarshaller = MarshallerFactory
                                                          .newMarshaller( kbase );
        }
    }

    protected abstract byte[] internalLoadStatefulKnowledgeSession(long sessionId);

    protected abstract int internalSaveStatefulKnowledgeSession(byte[] byteArray);
    
    protected abstract void internalUpdateStatefulKnowledgeSession(long id,
                                                                   byte[] byteArray);

}
