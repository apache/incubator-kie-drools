package org.drools.drlonyaml.model;

import java.util.ArrayList;
import java.util.List;

import org.drools.drl.ast.descr.ExistsDescr;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = Exists.class) // see https://stackoverflow.com/a/34128468/893991 TODO maybe enforce this check somehow
public class Exists implements Base {
    @JsonProperty(required = true)
    private List<Base> exists = new ArrayList<>();
    
    public static Exists from(ExistsDescr o) {
        Exists result = new Exists();
        result.exists = Utils.from(o.getDescrs());
        return result;
    }
    
    public List<Base> getExists() {
        return exists;
    }
}
