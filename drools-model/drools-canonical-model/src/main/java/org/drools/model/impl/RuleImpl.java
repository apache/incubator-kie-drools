package org.drools.model.impl;

import org.drools.model.Consequence;
import org.drools.model.Rule;
import org.drools.model.View;
import org.drools.model.patterns.CompositePatterns;

import java.util.HashMap;
import java.util.Map;

public class RuleImpl implements Rule {

    public static final String DEFAULT_CONSEQUENCE_NAME = "default";

    private final String pkg;
    private final String name;
    private final String unit;
    private final View view;
    private final Map<String, Consequence> consequences;

    private Map<Attribute, Object> attributes;

    public RuleImpl(String pkg, String name, String unit, View view, Consequence consequence, Map<Attribute, Object> attributes) {
        this.pkg = pkg;
        this.name = name;
        this.unit = unit;
        this.view = view;
        this.consequences = new HashMap<>();
        this.consequences.put( DEFAULT_CONSEQUENCE_NAME, consequence );
        this.attributes = attributes;
    }

    public RuleImpl( String pkg, String name, String unit, CompositePatterns view, Map<Attribute, Object> attributes) {
        this.pkg = pkg;
        this.name = name;
        this.unit = unit;
        this.view = view;
        this.consequences = view.getConsequences();
        this.attributes = attributes;
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public Consequence getDefaultConsequence() {
        return consequences.get(DEFAULT_CONSEQUENCE_NAME);
    }

    @Override
    public Map<String, Consequence> getConsequences() {
        return consequences;
    }

    @Override
    public <T> T getAttribute(Attribute<T> attribute) {
        T value = attributes != null ? (T) attributes.get(attribute) : null;
        return value != null ? value : attribute.getDefaultValue();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPackage() {
        return pkg;
    }

    @Override
    public String getUnit() {
        return unit;
    }
}
