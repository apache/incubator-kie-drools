package org.kie.dmn.core.compiler;

/**
 * for internal use
 */
public interface DMNDecisionLogicCompilerFactory {

    DMNDecisionLogicCompiler newDMNDecisionLogicCompiler(DMNCompilerImpl dmnCompiler, DMNCompilerConfigurationImpl dmnCompilerConfig);

}
