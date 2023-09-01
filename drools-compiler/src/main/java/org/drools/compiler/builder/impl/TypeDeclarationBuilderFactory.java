package org.drools.compiler.builder.impl;

import org.kie.api.internal.utils.KieService;

public interface TypeDeclarationBuilderFactory extends KieService {

    TypeDeclarationBuilder createTypeDeclarationBuilder(KnowledgeBuilderImpl kbuilder);

}
