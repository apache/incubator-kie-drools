package org.drools.base.rule.accessor;

/**
 * This interface is used by semantic modules that are compiled
 * to bytecode. 
 */
public interface CompiledInvoker
    extends
    Invoker {

    /**
     * Generated code should be able to return a String which represents the bytecode.
     * The elements in the list will be used to compare one semantic invoker
     * with another by making sure each item in the list is equivalent (equals()).
     * There are utilities in the ASM package to retrieve the bytecode for this.
     */
    String getMethodBytecode();

    static boolean isCompiledInvoker(Invoker invoker) {
        return invoker instanceof CompiledInvoker;
    }
}
