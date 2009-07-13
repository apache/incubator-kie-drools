package org.drools.command.impl;

import org.drools.builder.KnowledgeBuilder;
import org.drools.command.Context;
import org.drools.reteoo.ReteooWorkingMemory;

public interface GenericCommand<T> extends org.drools.command.Command {
	
	T execute(Context context);
	
}
