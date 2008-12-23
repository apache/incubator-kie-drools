package org.drools.definition.pipeline;

import org.drools.runtime.pipeline.PipelineContext;

public interface Receiver {
	void signal(Object object, PipelineContext context);
}
