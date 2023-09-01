package org.drools.drlonyaml.model;

import java.util.Objects;

import org.drools.drl.ast.descr.GlobalDescr;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"type", "id"})
public class Global {
    @JsonProperty(required = true)
    private String type;
    @JsonProperty(required = true)
    private String id;

    public static Global from(GlobalDescr r) {
        Objects.requireNonNull(r);
        Global result = new Global();
        result.type = r.getType();
        result.id = r.getIdentifier();
        return result;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }
}
