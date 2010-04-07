package org.drools.runtime.pipeline.impl;

import org.drools.grid.ExecutionNode;
import org.drools.runtime.pipeline.Pipeline;
import org.drools.runtime.pipeline.ResultHandler;

public class ExecutionNodePipelineImpl extends BaseEmitter implements Pipeline {
	private ExecutionNode node;

	public ExecutionNodePipelineImpl(ExecutionNode node) {
		this.node = node;
	}

	public synchronized void insert(Object object, ResultHandler resultHandler) {
		emit(object, new ExecutionNodePipelineContextImpl(this.node, null, resultHandler));
	}

}
