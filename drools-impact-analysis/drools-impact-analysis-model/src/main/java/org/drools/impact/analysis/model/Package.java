package org.drools.impact.analysis.model;

import java.util.List;

import static java.util.stream.Collectors.joining;

public class Package {

    private final String name;
    private final List<Rule> rules;

    public Package( String name, List<Rule> rules ) {
        this.rules = rules;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Rule> getRules() {
        return rules;
    }

    @Override
    public String toString() {
        return "Package{" +
                "name='" + name + '\'' +
                ",\n rules=" + rules.stream().map( Object::toString ).collect( joining("\n", ",\n", "") ) +
                '}';
    }
}
