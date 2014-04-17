package org.jbpm.process.builder;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.ProcessBuilderFactoryService;
import org.jbpm.compiler.ProcessBuilderImpl;
import org.kie.internal.builder.KnowledgeBuilder;

public class ProcessBuilderFactoryServiceImpl implements ProcessBuilderFactoryService {

	public ProcessBuilderImpl newProcessBuilder(KnowledgeBuilder knowledgeBuilder) {
		return new ProcessBuilderImpl((KnowledgeBuilderImpl) knowledgeBuilder);
	}
	
}
