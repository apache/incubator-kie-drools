package org.kie.dmn.core.impl;

import org.drools.compiler.compiler.BaseKnowledgeBuilderResultImpl;
import org.drools.compiler.compiler.DroolsError;
import org.kie.api.io.Resource;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.ResultSeverity;


public class DMNKnowledgeBuilderError extends DroolsError {

    private int[] lines = new int[0];
    private String message;
    private String namespace;
    
    public DMNKnowledgeBuilderError(Resource resource, String namespace, String message) {
        super(resource);
        this.namespace = namespace;
        this.message = message;
    }
    
    public DMNKnowledgeBuilderError(Resource resource, String message) {
        this(resource, "", message);
    }

    @Override
    public ResultSeverity getSeverity() {
        return ResultSeverity.ERROR;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public int[] getLines() {
        return lines;
    }

    @Override
    public String getNamespace() {
        return this.namespace;
    }

    
}
