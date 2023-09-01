package org.drools.compiler.compiler;

import org.drools.drl.parser.BaseKnowledgeBuilderResultImpl;
import org.kie.internal.builder.ResultSeverity;
import org.kie.api.io.Resource;

public abstract class DroolsWarning extends BaseKnowledgeBuilderResultImpl {

    public DroolsWarning(Resource resource) {
        super(resource);
    }

    @Override
    public ResultSeverity getSeverity() {
        return ResultSeverity.WARNING;
    }
}
