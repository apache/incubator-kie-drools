package org.drools.model.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.model.Consequence;
import org.drools.model.Rule;
import org.drools.model.RuleItemBuilder;
import org.drools.model.consequences.ConsequenceBuilder;
import org.drools.model.functions.Function1;
import org.drools.model.patterns.CompositePatterns;
import org.drools.model.view.ViewItemBuilder;

import static org.drools.model.impl.ViewBuilder.viewItems2Patterns;

public class RuleBuilder {

    public static final String DEFAULT_PACKAGE = "defaultpkg";

    private final String pkg;
    private final String name;

    private String unit;

    private final Map<Rule.Attribute, Object> attributes = new HashMap<>();

    public RuleBuilder(String name) {
        this(DEFAULT_PACKAGE, name);
    }

    public RuleBuilder(String pkg, String name) {
        this.pkg = pkg;
        this.name = name;
    }

    public RuleBuilder unit(String unit) {
        this.unit = unit;
        return this;
    }

    public RuleBuilder unit(Class<?> unitClass) {
        this.unit = unitClass.getName();
        return this;
    }

    public static String getCanonicalSimpleName(Class<?> c) {
        Class<?> enclosingClass = c.getEnclosingClass();
        return enclosingClass != null ?
               getCanonicalSimpleName(enclosingClass) + "." + c.getSimpleName() :
               c.getSimpleName();
    }

    public RuleBuilder attribute(Rule.Attribute attribute, Object value) {
        attributes.put(attribute, value);
        return this;
    }

    public RuleBuilderWithLHS view( ViewItemBuilder... viewItemBuilders ) {
        return new RuleBuilderWithLHS(viewItems2Patterns( viewItemBuilders ));
    }

    public Rule build( RuleItemBuilder... viewItemBuilders ) {
        CompositePatterns view = viewItems2Patterns( viewItemBuilders );
        for (Consequence consequence : view.getConsequences().values()) {
            view.ensureVariablesDeclarationInView( consequence.getDeclarations() );
        }
        return new RuleImpl(pkg, name, unit, view, view.getConsequences(), attributes);
    }

    public class RuleBuilderWithLHS {
        private final CompositePatterns view;

        public RuleBuilderWithLHS(CompositePatterns view) {
            this.view = view;
        }

        public Rule then(Function1<ConsequenceBuilder, ConsequenceBuilder.ValidBuilder> builder) {
            return then( builder.apply(new ConsequenceBuilder()) );
        }

        public Rule then(ConsequenceBuilder.ValidBuilder builder) {
            Consequence consequence = builder.get();
            view.ensureVariablesDeclarationInView( consequence.getDeclarations() );
            return new RuleImpl(pkg, name, unit, view, consequence, attributes);
        }
    }
}
