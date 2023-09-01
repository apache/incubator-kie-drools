package org.kie.dmn.feel.lang.types;

import org.kie.dmn.feel.lang.Scope;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.runtime.FEELFunction;

public class FunctionSymbol
        extends BaseSymbol {

    private FEELFunction evaluator;

    public FunctionSymbol(String id, FEELFunction evaluator) {
        super( id );
        this.evaluator = evaluator;
    }

    public FunctionSymbol(String id, Type type, FEELFunction evaluator) {
        super( id, type );
        this.evaluator = evaluator;
    }

    public FunctionSymbol(String id, Scope scope, FEELFunction evaluator) {
        super( id, scope );
        this.evaluator = evaluator;
    }

    public FunctionSymbol(String id, Type type, Scope scope, FEELFunction evaluator) {
        super( id, type, scope );
        this.evaluator = evaluator;
    }

    public FEELFunction getEvaluator() {
        return evaluator;
    }
}
