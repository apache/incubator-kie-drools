package org.kie.dmn.feel.lang.impl;

import org.kie.dmn.feel.lang.Property;
import org.kie.dmn.feel.lang.Type;


public class PropertyImpl implements Property {
    private final String name;
    private final Type type;
    
    public PropertyImpl(String name, Type type) {
        super();
        this.name = name;
        this.type = type;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public Type getType() {
        return type;
    }
}
