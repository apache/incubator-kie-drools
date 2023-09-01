
package org.drools.compiler.compiler;

import org.drools.drl.parser.BaseKnowledgeBuilderResultImpl;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.ResultSeverity;
import org.kie.internal.builder.conf.KBuilderSeverityOption;
import org.kie.api.io.Resource;


/**
 *
 */
public abstract class ConfigurableSeverityResult extends BaseKnowledgeBuilderResultImpl {
    
    public ConfigurableSeverityResult(Resource resource, KnowledgeBuilderConfiguration config) {
        super(resource);
        severity = config.getOption(KBuilderSeverityOption.KEY, getOptionKey()).getSeverity();
    }
    
    private ResultSeverity severity;
    /* (non-Javadoc)
     * @see org.kie.compiler.DroolsProblem#getProblemType()
     */
    @Override
    public ResultSeverity getSeverity() {
        return severity;
    }
    
    protected abstract String getOptionKey();
  
}
