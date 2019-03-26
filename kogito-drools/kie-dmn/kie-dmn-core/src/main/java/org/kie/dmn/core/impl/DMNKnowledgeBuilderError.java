package org.kie.dmn.core.impl;

import org.drools.compiler.compiler.DroolsError;
import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.internal.builder.InternalMessage;
import org.kie.internal.builder.ResultSeverity;


public class DMNKnowledgeBuilderError extends DroolsError {

    private int[] lines = new int[0];
    private String message;
    private String namespace;
    private ResultSeverity severity;
    private DMNMessage dmnMessage;
    
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

    /**
     * Builds a DMNKnowledgeBuilderError from a DMNMessage associated with the given Resource
     * @param resource the DMN model resource
     * @param namespace 
     * @param m the DMNMessage belonging to the given DMN model resource
     * @return
     */
    public static DMNKnowledgeBuilderError from(Resource resource, String namespace, DMNMessage m) {
        ResultSeverity rs = ResultSeverity.ERROR;
        switch (m.getLevel()) {
            case ERROR:
                rs = ResultSeverity.ERROR;
                break;
            case INFO:
                rs = ResultSeverity.INFO;
                break;
            case WARNING:
                rs = ResultSeverity.WARNING;
                break;
            default:
                rs = ResultSeverity.ERROR;
                break;
        }
        DMNKnowledgeBuilderError res = new DMNKnowledgeBuilderError(rs, resource, namespace, m.getMessage());
        res.dmnMessage = m;
        return res;
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

    public DMNMessage getDmnMessage() {
        return dmnMessage;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DMNKnowledgeBuilderError [message=");
        sb.append(message);
        sb.append(", namespace=");
        sb.append(namespace);
        sb.append(", dmnMessage=");
        sb.append(dmnMessage);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public InternalMessage asMessage(long id) {
        if (dmnMessage == null) {
            return super.asMessage(id);
        } else {
            return ((DMNMessageImpl) dmnMessage).cloneWith(id, getResource().getSourcePath());
        }
    }
    
}
