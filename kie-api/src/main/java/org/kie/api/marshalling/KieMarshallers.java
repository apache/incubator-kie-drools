package org.kie.api.marshalling;

import org.kie.api.KieBase;
import org.kie.api.internal.utils.KieService;

/**
 *
 * <p>This api is experimental and thus the classes and the interfaces returned are subject to change.</p>
 */
public interface KieMarshallers extends KieService {
    ObjectMarshallingStrategyAcceptor newClassFilterAcceptor(String[] patterns);

    ObjectMarshallingStrategy newIdentityMarshallingStrategy();

    ObjectMarshallingStrategy newIdentityMarshallingStrategy(ObjectMarshallingStrategyAcceptor acceptor);

    ObjectMarshallingStrategy newSerializeMarshallingStrategy();

    ObjectMarshallingStrategy newSerializeMarshallingStrategy(ObjectMarshallingStrategyAcceptor acceptor);

    /**
     * The marshalling strategies for this method are undefined and thus they are derived from the ksession's or environment
     * provided.
     *
     * @param kbase
     * @return marshaller created for the specified KieBase
     */
    Marshaller newMarshaller(KieBase kbase);

    /**
     * This will override the strategies specified in the ksession or environment.
     *
     * @param kbase
     * @param strategies
     * @return marshaller created for the specified KieBase with the custom marshalling strategies
     */
    Marshaller newMarshaller(KieBase kbase,
                             ObjectMarshallingStrategy[] strategies);
}
