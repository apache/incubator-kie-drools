package org.kie.dmn.ruleset2dmn;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.CompoundPredicate.BooleanOperator;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.SimplePredicate;
import org.dmg.pmml.rule_set.SimpleRule;

public class SimpleRuleRow {
    final SimpleRule r;
    final Map<String, List<SimplePredicate>> map = new LinkedHashMap<>();

    public SimpleRuleRow(SimpleRule r) {
        this.r = r;
        Predicate rootPredicate = r.getPredicate();
        if (rootPredicate instanceof SimplePredicate) {
            SimplePredicate sp = (SimplePredicate) rootPredicate;
            map.computeIfAbsent(sp.getField().getValue(), k -> new ArrayList<SimplePredicate>()).add(sp);
        } else {
            if (!(rootPredicate instanceof CompoundPredicate)) {
                throw new UnsupportedOperationException("Was expecting a CompoundPredicate, found: "+rootPredicate.getClass());
            }
            CompoundPredicate cPredicate = (CompoundPredicate) rootPredicate;
            if (!(cPredicate.getBooleanOperator() == BooleanOperator.AND)){
                throw new UnsupportedOperationException("Only AND operator usage is supported in CompoundPredicate to convert to a Decision Table.");
            }
            for (Predicate c : cPredicate.getPredicates()) {
                SimplePredicate sp = (SimplePredicate) c;
                map.computeIfAbsent(sp.getField().getValue(), k -> new ArrayList<SimplePredicate>()).add(sp);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(r.getId()).append(" -> ").append(r.getScore()).append("\n");
        for (Entry<String, List<SimplePredicate>> kv : map.entrySet()) {
            sb.append(kv.getKey()).append(" ");
            for (SimplePredicate v : kv.getValue()) {
                sb.append(v.getOperator()).append(v.getValue()).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}