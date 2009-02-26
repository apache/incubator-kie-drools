package org.drools.marshalling;

import org.drools.KnowledgeBase;

public interface MarshallerProvider {
    ObjectMarshallingStrategyAcceptor newClassFilterAcceptor(String[] patterns);

    ObjectMarshallingStrategy newIdentityMarshallingStrategy();

    ObjectMarshallingStrategy newIdentityMarshallingStrategy(ObjectMarshallingStrategyAcceptor acceptor);

    ObjectMarshallingStrategy newSerializeMarshallingStrategy();

    ObjectMarshallingStrategy newSerializeMarshallingStrategy(ObjectMarshallingStrategyAcceptor acceptor);

    Marshaller newMarshaller(KnowledgeBase kbase);

    Marshaller newMarshaller(KnowledgeBase kbase,
                             ObjectMarshallingStrategy[] strategies);
}
