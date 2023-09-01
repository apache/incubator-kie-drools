package org.drools.serialization.protobuf.marshalling;

import org.kie.api.marshalling.Marshaller;

public interface InternalMarshaller extends Marshaller {

    KieSessionInitializer getInitializer();

    void setInitializer( KieSessionInitializer initializer );
}
