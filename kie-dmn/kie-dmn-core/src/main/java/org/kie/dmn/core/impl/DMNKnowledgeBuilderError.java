package org.kie.dmn.core.impl;

import org.drools.compiler.compiler.DroolsError;
import org.kie.api.io.Resource;
import org.kie.internal.builder.ResultSeverity;


public class DMNKnowledgeBuilderError extends DroolsError {

    private int[] lines = new int[0];
    private String message;
    private String namespace;
    private ResultSeverity severity;
    
    public DMNKnowledgeBuilderError(ResultSeverity severity, Resource resource, String namespace, String message) {
        super(resource);
        this.severity = severity;
        this.namespace = namespace;
        this.message = message;
    }
    
    public DMNKnowledgeBuilderError(ResultSeverity severity, Resource resource, String message) {
        this(severity, resource, "", message);
    }

    public DMNKnowledgeBuilderError(ResultSeverity severity, String message) {
        this(severity, null, "", message);
    }

    @Override
    public ResultSeverity getSeverity() {
        return this.severity;
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
