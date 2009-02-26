package org.drools.marshalling;

import org.drools.KnowledgeBase;
import org.drools.ProviderInitializationException;

public class MarshallerFactory {
    private static volatile MarshallerProvider provider;

    public static ObjectMarshallingStrategyAcceptor newClassFilterAcceptor(String[] patterns) {
        return getMarshallerProvider().newClassFilterAcceptor( patterns );
    }

    public static ObjectMarshallingStrategy newIdentityMarshallingStrategy() {
        return getMarshallerProvider().newIdentityMarshallingStrategy();
    }

    public static ObjectMarshallingStrategy newIdentityMarshallingStrategy(ObjectMarshallingStrategyAcceptor acceptor) {
        return getMarshallerProvider().newIdentityMarshallingStrategy( acceptor );
    }

    public static ObjectMarshallingStrategy newSerializeMarshallingStrategy() {
        return getMarshallerProvider().newSerializeMarshallingStrategy();
    }

    public static ObjectMarshallingStrategy newSerializeMarshallingStrategy(ObjectMarshallingStrategyAcceptor acceptor) {
        return getMarshallerProvider().newSerializeMarshallingStrategy( acceptor );
    }

    /**
     * Default uses the serialise marshalling strategy.
     * @return
     */
    public static Marshaller newMarshaller(KnowledgeBase kbase) {
        return getMarshallerProvider().newMarshaller( kbase );
    }

    public static Marshaller newMarshaller(KnowledgeBase kbase,
                                           ObjectMarshallingStrategy[] strategies) {
        return getMarshallerProvider().newMarshaller( kbase,
                                                      strategies );
    }

    private static synchronized void setMarshallerProvider(MarshallerProvider provider) {
        MarshallerFactory.provider = provider;
    }

    private static synchronized MarshallerProvider getMarshallerProvider() {
        if ( provider == null ) {
            loadProvider();
        }
        return provider;
    }

    private static void loadProvider() {
        try {
            Class<MarshallerProvider> cls = (Class<MarshallerProvider>) Class.forName( "org.drools.marshalling.impl.MarshallerProviderImpl" );
            setMarshallerProvider( cls.newInstance() );
        } catch ( Exception e2 ) {
            throw new ProviderInitializationException( "Provider org.drools.marshalling.impl.MarshallerProviderImpl could not be set.",
                                                       e2 );
        }
    }
}
