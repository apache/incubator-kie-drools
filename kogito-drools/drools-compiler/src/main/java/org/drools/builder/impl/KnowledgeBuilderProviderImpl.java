package org.drools.builder.impl;

import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderProvider;
import org.drools.compiler.PackageBuilder;

public class KnowledgeBuilderProviderImpl implements KnowledgeBuilderProvider {

	public KnowledgeBuilder newKnowledgeBuilder() {
		return new KnowledgeBuilderImpl( new PackageBuilder() );
	}


}
