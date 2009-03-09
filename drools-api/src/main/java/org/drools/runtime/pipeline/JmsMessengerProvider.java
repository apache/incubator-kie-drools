package org.drools.runtime.pipeline;

import java.util.Properties;

/**
 * 
 * <p>This api is experimental and thus the classes and the interfaces returned are subject to change.</p>
 *
 */
public interface JmsMessengerProvider {
    Service newJmsMessenger(Pipeline pipeline,
                            Properties properties,
                            String destinationName,
                            ResultHandlerFactory resultHandlerFactory);

    Action newJmsUnwrapMessageObject();
}
