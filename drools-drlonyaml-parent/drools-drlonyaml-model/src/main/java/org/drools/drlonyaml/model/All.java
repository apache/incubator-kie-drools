package org.drools.drlonyaml.model;

import java.util.List;

import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.ExistsDescr;
import org.drools.drl.ast.descr.NotDescr;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = All.class) // see https://stackoverflow.com/a/34128468/893991 TODO maybe enforce this check somehow
public class All implements Base {
    private List<Base> exists;
    
    public static All from(AndDescr o) {
        All result = new All();
        result.exists = Utils.from(o.getDescrs());
        return result;
    }
    
    public List<Base> getExists() {
        return exists;
    }
}
