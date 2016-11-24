package org.kie.dmn.feel.lang.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.dmn.feel.lang.CustomType;
import org.kie.dmn.feel.lang.Property;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.parser.feel11.ParserHelper;

public class MapBackedType implements CustomType {
    
    private Map<String, Property> properties = new HashMap<>();
    
    public MapBackedType() {
    }

    /**
     * Utility constructor by reflection over key-value pairs.
     * @param fields
     */
    public MapBackedType(Map<String, ?> map) {
        map.entrySet().stream()
            .map( kv -> new PropertyImpl( kv.getKey(), ParserHelper.determineTypeFromClass( kv.getValue().getClass()) ) )
            .forEach( f -> properties.put( f.getName(), f ) );
    }
    
    public MapBackedType(List<Property> properties) {
        properties.stream().forEach(p -> this.properties.put(p.getName(), p) );
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Object fromString(String value) {
        return null;
    }

    @Override
    public String toString(Object value) {
        return null;
    }
    
    public MapBackedType addField(String name, Type type) {
        properties.put( name, new PropertyImpl(name, type) );
        return this;
    }

    @Override
    public Map<String, Property> getProperties() {
        return properties;
    }
}
