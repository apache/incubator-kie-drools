package org.drools.drlonyaml.model;

import java.util.ArrayList;
import java.util.List;

import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.ExistsDescr;
import org.drools.drl.ast.descr.NotDescr;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = All.class) // see https://stackoverflow.com/a/34128468/893991 TODO maybe enforce this check somehow
public class All implements Base {
    @JsonProperty(required = true)
    private List<Base> all = new ArrayList<>();
    
    public static All from(AndDescr o) {
        All result = new All();
        result.all = Utils.from(o.getDescrs());
        return result;
    }
    
    public List<Base> getAll() {
        return all;
    }
}
