package org.codehaus.jfdi.interpreter;

import java.util.List;

public class ListValue extends AnonListValue
    implements
    VariableValueHandler {

    private final String identifier;

    public ListValue(List list, String identifier) {
        super( list );
        this.identifier = identifier;
        
    }

    public String getIdentifier() {
        return identifier;
    }

}
