package org.jbpm.process.builder;

import org.drools.compiler.compiler.PackageBuilder;
import org.drools.compiler.compiler.ProcessBuilderFactoryService;
import org.jbpm.compiler.ProcessBuilderImpl;

public class ProcessBuilderFactoryServiceImpl implements ProcessBuilderFactoryService {

	public ProcessBuilderImpl newProcessBuilder(PackageBuilder packageBuilder) {
		return new ProcessBuilderImpl(packageBuilder);
	}
	
}
