package org.drools.modelcompiler.builder;

import java.util.List;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.core.reteoo.Rete;

public interface AdditionalFileGenerator {

    List<GeneratedFile> additionalFiles(KnowledgeBuilderConfigurationImpl builderConfiguration, Rete rete);
}
