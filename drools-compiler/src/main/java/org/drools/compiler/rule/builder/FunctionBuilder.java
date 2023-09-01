package org.drools.compiler.rule.builder;

import java.util.List;
import java.util.Map;

import org.drools.drl.ast.descr.FunctionDescr;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.rule.LineMappings;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.drools.util.TypeResolver;

public interface FunctionBuilder extends EngineElementBuilder {

    public String build(final InternalKnowledgePackage pkg,
                        final FunctionDescr functionDescr,
                        final TypeResolver typeResolver,
                        final Map<String, LineMappings> lineMappings,
                        final List<KnowledgeBuilderResult> errors);
}
