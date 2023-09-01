package org.drools.serialization.protobuf;

import org.drools.core.marshalling.ClassObjectMarshallingStrategyAcceptor;
import org.drools.serialization.protobuf.marshalling.IdentityPlaceholderResolverStrategy;
import org.drools.serialization.protobuf.marshalling.MarshallingConfigurationImpl;
import org.drools.core.marshalling.SerializablePlaceholderResolverStrategy;
import org.kie.api.KieBase;
import org.kie.api.marshalling.KieMarshallers;
import org.kie.api.marshalling.Marshaller;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.marshalling.ObjectMarshallingStrategyAcceptor;

public class MarshallerProviderImpl implements KieMarshallers {

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
    
    public Marshaller newMarshaller(KieBase kbase) {
        return newMarshaller(kbase, null );
    }
    
    public Marshaller newMarshaller(KieBase kbase, ObjectMarshallingStrategy[] strategies) {
        return new ProtobufMarshaller( kbase , new MarshallingConfigurationImpl( strategies, true, true ) );
    }
    
}
