package org.drools.drlonyaml.model;

import org.drools.drl.ast.descr.ImportDescr;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class Import {
    @JsonValue
    private String target;
    
    @JsonCreator
    public Import(final String target) {
        this.target = target;
    }
    
    private Import() {
        // no-arg.
    }

    public static Import from(ImportDescr i) {
        Import result = new Import();
        result.target = i.getTarget();
        return result;
    }

    public String getTarget() {
        return target;
    }
}
