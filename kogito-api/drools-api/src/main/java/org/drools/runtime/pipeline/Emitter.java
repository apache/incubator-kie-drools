package org.drools.runtime.pipeline;

public interface Emitter {
	void setReceiver(Receiver receiver);

	Receiver getReceiver();	
}
