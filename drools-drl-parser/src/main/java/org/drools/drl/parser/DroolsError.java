package org.drools.drl.parser;

import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.ResultSeverity;
import org.kie.api.io.Resource;

public abstract class DroolsError extends BaseKnowledgeBuilderResultImpl implements KnowledgeBuilderError {

    public DroolsError() {
        this(null);
    }

    public DroolsError(Resource resource) {
        super(resource);
    }

    public ResultSeverity getSeverity() {
        return ResultSeverity.ERROR;
    }

    /**
     * Default implementation (overriden where possible and meaningful) returning just an empty string
     * @return
     */
    public String getNamespace() {
        return "";
    }
}
