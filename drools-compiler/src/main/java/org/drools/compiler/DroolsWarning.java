package org.drools.compiler;

import org.drools.builder.ResultSeverity;
import org.drools.io.Resource;

public abstract class DroolsWarning extends BaseKnowledgeBuilderResultImpl {

    @Override
    public ResultSeverity getSeverity() {
        return ResultSeverity.WARNING;
    }
}
