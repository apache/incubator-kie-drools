package org.drools.runtime.pipeline;

import java.util.Properties;

public interface JmsMessengerProvider {
    Service newJmsMessenger(Pipeline pipeline,
                            Properties properties,
                            String destinationName,
                            ResultHandlerFactory resultHandlerFactory);
}
