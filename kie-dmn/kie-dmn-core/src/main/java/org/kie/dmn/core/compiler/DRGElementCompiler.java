package org.kie.dmn.core.compiler;

import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.model.api.DRGElement;

public interface DRGElementCompiler {
    
    boolean accept( DRGElement de );
    void compileNode( DRGElement de, DMNCompilerImpl compiler, DMNModelImpl model);
    
    default boolean accept( DMNNode node ) {
        return false;
    }
    default void compileEvaluator(DMNNode node, DMNCompilerImpl compiler, DMNCompilerContext ctx, DMNModelImpl model) {
        // by default no evaluator.
    }
}