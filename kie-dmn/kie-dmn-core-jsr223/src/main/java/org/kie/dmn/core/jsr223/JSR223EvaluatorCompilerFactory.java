package org.kie.dmn.core.jsr223;

import org.kie.dmn.core.compiler.DMNCompilerConfigurationImpl;
import org.kie.dmn.core.compiler.DMNCompilerImpl;
import org.kie.dmn.core.compiler.DMNDecisionLogicCompiler;
import org.kie.dmn.core.compiler.DMNDecisionLogicCompilerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSR223EvaluatorCompilerFactory implements DMNDecisionLogicCompilerFactory {
    private static final Logger LOG = LoggerFactory.getLogger( JSR223EvaluatorCompilerFactory.class );

    @Override
    public DMNDecisionLogicCompiler newDMNDecisionLogicCompiler(DMNCompilerImpl dmnCompiler, DMNCompilerConfigurationImpl dmnCompilerConfig) {
        LOG.debug("Instantiating JSR223EvaluatorCompilerFactory");
        return new JSR223EvaluatorCompiler(dmnCompiler);
    }

}