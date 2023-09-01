package org.drools.drlonyaml.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.RuleDescr;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"name", "when", "then"})
public class Rule {
    @JsonProperty(required = true)
    private String name;
    @JsonProperty(required = true)
    private List<Base> when = new ArrayList<>();
    @JsonProperty(required = true)
    private AbstractThen then;

    public static Rule from(RuleDescr r) {
        Objects.requireNonNull(r);
        Rule result = new Rule();
        result.name = r.getName();
        for (BaseDescr dd: r.getLhs().getDescrs()) {
            result.when.add((Base) Utils.from(dd));
        }
        result.then = StringThen.from(r.getConsequence().toString());
        return result;
    }

    public String getName() {
        return name;
    }

    public List<Base> getWhen() {
        return when;
    }

    public Object getThen() {
        return then;
    }
}
