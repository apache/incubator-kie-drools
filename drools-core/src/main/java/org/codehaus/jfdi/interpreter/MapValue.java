package org.codehaus.jfdi.interpreter;


/** This is a map value that is a variable (still inline, but it has been assigned to something */
public class MapValue extends AnonMapValue implements VariableValueHandler {

    private final String identifier;

    public MapValue(KeyValuePair[] pairs, String identifier) {
        super( pairs );
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

}
