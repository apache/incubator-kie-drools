package org.kie.dmn.feel.lang.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.kie.dmn.feel.lang.CustomType;
import org.kie.dmn.feel.lang.FEELProperty;
import org.kie.dmn.feel.lang.Property;
import org.kie.dmn.feel.parser.feel11.ParserHelper;

public class JavaBackedType implements CustomType {
    private Class<?> wrapped;
    private Map<String, Property> properties = new HashMap<>();

    public JavaBackedType(Class<?> class1) {
        this.wrapped = class1;
        Stream.of( class1.getMethods() )
            .filter( m -> m.getAnnotation(FEELProperty.class) != null )
            .forEach( m -> properties.put( m.getAnnotation(FEELProperty.class).value() , new PropertyImpl( m.getAnnotation(FEELProperty.class).value() , ParserHelper.determineTypeFromClass(m.getReturnType()) ) ) );
            ;
    }

    @Override
    public String getName() {
        return wrapped.getName();
    }

    @Override
    public Object fromString(String value) {
        return null;
    }

    @Override
    public String toString(Object value) {
        return null;
    }

    public Class<?> getWrapped() {
        return wrapped;
    }

    @Override
    public Map<String, Property> getProperties() {
        return this.properties;
    }
}
