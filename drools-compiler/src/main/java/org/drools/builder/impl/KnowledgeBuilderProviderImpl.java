package org.drools.builder.impl;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseProvider;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderProvider;
import org.drools.compiler.PackageBuilder;
import org.drools.spi.KnowledgeHelper;

public class KnowledgeBuilderProviderImpl implements KnowledgeBuilderProvider {

	public KnowledgeBuilder newKnowledgeBuilder() {
		return new KnowledgeBuilderImpl( new PackageBuilder() );
	}


}
