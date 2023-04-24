package org.drools.drlonyaml.model;

import java.util.ArrayList;
import java.util.List;

import org.drools.drl.ast.descr.ExistsDescr;
import org.drools.drl.ast.descr.NotDescr;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = Not.class) // see https://stackoverflow.com/a/34128468/893991 TODO maybe enforce this check somehow
public class Not implements Base {
    @JsonProperty(required = true)
    private List<Base> not = new ArrayList<>();
    
    public static Not from(NotDescr o) {
        Not result = new Not();
        result.not = Utils.from(o.getDescrs());
        return result;
    }
    
    public List<Base> getNot() {
        return not;
    }
}
