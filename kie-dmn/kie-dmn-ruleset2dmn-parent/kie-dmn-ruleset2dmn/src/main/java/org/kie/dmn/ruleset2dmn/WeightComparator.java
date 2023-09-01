package org.kie.dmn.ruleset2dmn;

import java.util.Comparator;

import org.dmg.pmml.rule_set.SimpleRule;

public class WeightComparator implements Comparator<SimpleRule> {

    @Override
    public int compare(SimpleRule o1, SimpleRule o2) {
        return Double.compare(o1.getWeight().doubleValue(), o2.getWeight().doubleValue());
    }

}