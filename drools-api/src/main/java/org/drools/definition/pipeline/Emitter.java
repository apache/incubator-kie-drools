package org.drools.definition.pipeline;

import java.util.Collection;

public interface Emitter {
	void addReceiver(Receiver receiver);

	void removeReceiver(Receiver receiver);
	
	Collection<Receiver> getReceivers();
	
}
