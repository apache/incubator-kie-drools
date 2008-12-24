package org.drools.runtime.pipeline;


public interface Receiver {
	void signal(Object object, PipelineContext context);
}
