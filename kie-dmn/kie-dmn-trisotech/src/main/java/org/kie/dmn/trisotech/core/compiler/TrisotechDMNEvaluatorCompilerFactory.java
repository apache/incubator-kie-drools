package org.kie.dmn.trisotech.core.compiler;

import org.kie.dmn.core.compiler.DMNCompilerConfigurationImpl;
import org.kie.dmn.core.compiler.DMNCompilerImpl;
import org.kie.dmn.core.compiler.DMNDecisionLogicCompiler;
import org.kie.dmn.core.compiler.DMNDecisionLogicCompilerFactory;

public class TrisotechDMNEvaluatorCompilerFactory implements DMNDecisionLogicCompilerFactory {

    @Override
    public DMNDecisionLogicCompiler newDMNDecisionLogicCompiler(DMNCompilerImpl dmnCompiler, DMNCompilerConfigurationImpl dmnCompilerConfig) {
        return new TrisotechDMNEvaluatorCompiler(dmnCompiler);
    }

}
