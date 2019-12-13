package org.drools.model.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drools.model.Consequence;
import org.drools.model.Rule;
import org.drools.model.View;
import org.drools.model.patterns.CompositePatterns;

public class RuleImpl implements Rule, ModelComponent {

    public static final String DEFAULT_CONSEQUENCE_NAME = "default";

    private final String pkg;
    private final String name;
    private final String unit;
    private final View view;
    private final Map<String, Consequence> consequences;

    private Map<Attribute, Object> attributes;
    private Map<String, Object> metaAttributes;

    public RuleImpl(String pkg, String name, String unit, View view, Consequence consequence, Map<Attribute, Object> attributes, Map<String, Object> metaAttributes) {
        this.pkg = pkg;
        this.name = name;
        this.unit = unit;
        this.view = view;
        this.consequences = new HashMap<>();
        this.consequences.put( DEFAULT_CONSEQUENCE_NAME, consequence );
        this.attributes = attributes;
        this.metaAttributes = metaAttributes;
    }

    public RuleImpl(String pkg, String name, String unit, CompositePatterns view, Map<Attribute, Object> attributes, Map<String, Object> metaAttributes) {
        this.pkg = pkg;
        this.name = name;
        this.unit = unit;
        this.view = view;
        this.consequences = view.getConsequences();
        this.attributes = attributes;
        this.metaAttributes = metaAttributes;
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
    public Map<String, Object> getMetaData() {
        return Collections.unmodifiableMap(metaAttributes);
    }

    @Override
    public Object getMetaData(String name) {
        return metaAttributes.get(name);
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

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        RuleImpl rule = ( RuleImpl ) o;

        if ( !pkg.equals( rule.pkg ) ) return false;
        if ( !name.equals( rule.name ) ) return false;
        if ( unit != null ? !unit.equals( rule.unit ) : rule.unit != null ) return false;
        if ( !ModelComponent.areEqualInModel( view, rule.view ) ) return false;
        if ( !ModelComponent.areEqualInModel( consequences, rule.consequences ) ) return false;
        return attributes != null ? attributes.equals( rule.attributes ) : rule.attributes == null;
    }

    @Override
    public String toString() {
        return "Rule: " +  pkg + "." + name + " (view: " + view + ", consequences: " + consequences + ")";
    }
}
