package org.kie.dmn.feel.lang.types;

import org.kie.dmn.feel.lang.Scope;
import org.kie.dmn.feel.lang.Type;

public class VariableSymbol extends BaseSymbol {

    public VariableSymbol(String id) {
        super( id );
    }

    public VariableSymbol(String id, Type type) {
        super( id, type );
    }

    public VariableSymbol(String id, Scope scope) {
        super( id, scope );
    }

    public VariableSymbol(String id, Type type, Scope scope) {
        super( id, type, scope );
    }
}
