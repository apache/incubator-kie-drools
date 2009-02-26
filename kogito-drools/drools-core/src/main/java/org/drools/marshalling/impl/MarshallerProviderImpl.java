package org.drools.marshalling.impl;

import org.drools.KnowledgeBase;
import org.drools.marshalling.Marshaller;
import org.drools.marshalling.MarshallerProvider;
import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.marshalling.ObjectMarshallingStrategyAcceptor;

public class MarshallerProviderImpl implements MarshallerProvider {

    public ObjectMarshallingStrategyAcceptor newClassFilterAcceptor(String[] patterns) {
        return new ClassObjectMarshallingStrategyAcceptor( patterns );
    }

    public ObjectMarshallingStrategy newIdentityMarshallingStrategy() {
        return new IdentityPlaceholderResolverStrategy( ClassObjectMarshallingStrategyAcceptor.DEFAULT );
    }

    public ObjectMarshallingStrategy newIdentityMarshallingStrategy(ObjectMarshallingStrategyAcceptor acceptor) {
        return new IdentityPlaceholderResolverStrategy( acceptor );
    }



    public ObjectMarshallingStrategy newSerializeMarshallingStrategy() {
        return new SerializablePlaceholderResolverStrategy( ClassObjectMarshallingStrategyAcceptor.DEFAULT  );
    }

    public ObjectMarshallingStrategy newSerializeMarshallingStrategy(ObjectMarshallingStrategyAcceptor acceptor) {
        return new SerializablePlaceholderResolverStrategy( acceptor );
    }
    
    public Marshaller newMarshaller(KnowledgeBase kbase) {
        return newMarshaller(kbase, new ObjectMarshallingStrategy[] { newSerializeMarshallingStrategy() } );
    }    
    
    public Marshaller newMarshaller(KnowledgeBase kbase, ObjectMarshallingStrategy[] strategies) {   
        if ( strategies == null ) {
            throw new IllegalArgumentException( "Strategies should not be null" );
        }
        return new DefaultMarshaller( kbase , new MarshallingConfigurationImpl( strategies, true, true ) );
    }
    
}
