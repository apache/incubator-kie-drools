package org.kie.dmn.ruleset2dmn;

import java.util.Comparator;

import org.dmg.pmml.SimplePredicate;

public class MyComparablePredicateOnOperator implements Comparator<SimplePredicate> {

    @Override
    public int compare(SimplePredicate o1, SimplePredicate o2) {
        return o1.getOperator().name().compareTo(o2.getOperator().name());
    }


}
