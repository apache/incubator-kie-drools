package org.drools.drlonyaml.model;

import java.util.ArrayList;
import java.util.List;

import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.drl.ast.descr.RuleDescr;


public class Rule {
    private String name;
    private List<Base> when = new ArrayList<>();
    private AbstractThen then;

    public static Rule from(RuleDescr r) {
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
