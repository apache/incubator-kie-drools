package org.drools.model.impl;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import org.drools.model.Rule;
import org.drools.model.RuleItemBuilder;

import static org.drools.model.impl.ViewBuilder.viewItems2Patterns;

public class RuleBuilder {

    public static final String DEFAULT_PACKAGE = "defaultpkg";

    private final String pkg;
    private final String name;

    private String unit;

    private final Map<Rule.Attribute, Object> attributes = new IdentityHashMap<>();
    private Map<String, Object> metaAttributes = new HashMap<>();

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

    public <T> RuleBuilder attribute(Rule.Attribute<T> attribute, T value) {
        attributes.put(attribute, value);
        return this;
    }

    public RuleBuilder metadata(String key, Object value) {
        this.metaAttributes.put(key, value);
        return this;
    }

    public Rule build( RuleItemBuilder... viewItemBuilders ) {
        return new RuleImpl(pkg, name, unit, viewItems2Patterns(viewItemBuilders), attributes, metaAttributes);
    }
}
