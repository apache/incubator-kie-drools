package org.kie.dmn.core.compiler;

import org.kie.dmn.api.core.DMNType;

import java.util.HashMap;
import java.util.Map;

public class DMNScope {

    private DMNScope parent;
    private Map<String, DMNType> variables = new HashMap<>(  );

    public DMNScope() {
    }

    public DMNScope(DMNScope parent ) {
        this.parent = parent;
    }

    public void setVariable( String name, DMNType type ) {
        this.variables.put( name, type );
    }

    public DMNType resolve( String name ) {
        DMNType type = this.variables.get( name );
        if( type == null && this.parent != null ) {
            return this.parent.resolve( name );
        }
        return type;
    }

    public Map<String, DMNType> getVariables() {
        return this.variables;
    }
}
