package org.drools.drlonyaml.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class StringThen extends AbstractThen {
    @JsonValue
    private String then;
    
    @JsonCreator
    public StringThen(final String then) {
        this.then = then;
    }
    
    private StringThen() {
        // no-arg.
    }

    public static StringThen from(String then) {
        StringThen result = new StringThen();
        result.then = then;
        return result;
    }

    public String getThen() {
        return then;
    }
}