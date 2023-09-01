package org.drools.impact.analysis.graph;

import org.drools.impact.analysis.model.Rule;

public class AnalyzedRule {

    private final Rule rule;
    private ReactivityType reactivityType;

    public AnalyzedRule( Rule rule, boolean positive ) {
        this(rule, ReactivityType.decode(positive) );
    }

    public AnalyzedRule( Rule rule, ReactivityType reactivityType ) {
        this.rule = rule;
        this.reactivityType = reactivityType;
    }

    public Rule getRule() {
        return rule;
    }

    public ReactivityType getReactivityType() {
        return reactivityType;
    }

    public void combineReactivityType( ReactivityType reactivityType ) {
        this.reactivityType = this.reactivityType.combine( reactivityType );
    }
}
