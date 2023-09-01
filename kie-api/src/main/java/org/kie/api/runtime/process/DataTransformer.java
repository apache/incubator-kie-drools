package org.kie.api.runtime.process;

import java.util.Map;

/**
 * Data transformation capabilities used by data input and data output transformation
 * as part of BPMN2 process definition. It allows plugable implementations.
 *
 */
public interface DataTransformer {

    /**
     * Compiles given expression into reusable unit. This is optional operation and in case
     * it's not supported by given transformed it should return the same expression that was
     * given in the input. If compilation is supported a compiled instance of the expression
     * should be returned.
     * @param expression language specific expression
     * @param parameters parameters e.g. imports defined in the process
     * @return compiled expression instance or same as given as argument expression
     * @throws RuntimeException in case of unexpected errors during compilation
     */
    public Object compile(String expression, Map<String, Object> parameters);

    /**
     * Transforms the given expression object (might be compiled expression) using
     * <code>parameters</code> as contextual information (aka bindings) used during
     * evaluation of the expression.
     * @param expression expression to evaluate
     * @param parameters binding parameters
     * @return result of the evaluation
     * @throws RuntimeException in case of unexpected errors during evaluation of the expression
     */
    public Object transform(Object expression, Map<String, Object> parameters);

}
