package org.drools.runtime.pipeline;


public interface Receiver {
	void receive(Object object, PipelineContext context);
}
