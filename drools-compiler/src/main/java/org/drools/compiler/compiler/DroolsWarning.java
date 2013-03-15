package org.drools.compiler.compiler;

import org.kie.builder.ResultSeverity;
import org.kie.io.Resource;

public abstract class DroolsWarning extends BaseKnowledgeBuilderResultImpl {

    public DroolsWarning(Resource resource) {
        super(resource);
    }

    @Override
    public ResultSeverity getSeverity() {
        return ResultSeverity.WARNING;
    }
}
