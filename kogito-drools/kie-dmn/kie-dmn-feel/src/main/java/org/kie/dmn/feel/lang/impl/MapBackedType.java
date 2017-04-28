package org.kie.dmn.feel.lang.impl;

import org.kie.dmn.feel.lang.CompositeType;
import org.kie.dmn.feel.lang.Type;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A map-based type descriptor
 */
public class MapBackedType
        implements CompositeType {

    public static final String TYPE_NAME = "__TYPE_NAME__";

    private String            name   = "[anonymous]";
    private Map<String, Type> fields = new LinkedHashMap<>();

    public MapBackedType() {
    }

    public MapBackedType(String typeName) {
        this.name = typeName;
    }

    public MapBackedType(String typeName, Map<String, Type> fields) {
        this.name = typeName;
        this.fields.putAll( fields );
    }

    @Override
    public String getName() {
        return this.name;
    }

    public MapBackedType addField(String name, Type type) {
        fields.put( name, type );
        return this;
    }

    @Override
    public Map<String, Type> getFields() {
        return fields;
    }

    @Override
    public boolean isInstanceOf(Object o) {
        if ( o == null || !(o instanceof Map) ) {
            return false;
        }
        Map<?, ?> instance = (Map<?, ?>) o;
        for ( Entry<String, Type> f : fields.entrySet() ) {
            if ( !instance.containsKey(f.getKey()) ) {
                return false;
            }
            Object instanceValueForKey = instance.get(f.getKey());
            if ( instanceValueForKey != null && !f.getValue().isInstanceOf(instanceValueForKey) ) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isAssignableValue(Object value) {
        if ( value == null ) {
            return true;
        }
        if ( !(value instanceof Map) ) {
            return false;
        }
        Map<?, ?> instance = (Map<?, ?>) value;
        for ( Entry<String, Type> f : fields.entrySet() ) {
            if ( !instance.containsKey(f.getKey()) ) {
                return false;
            }
            Object instanceValueForKey = instance.get(f.getKey());
            if ( instanceValueForKey != null && !f.getValue().isAssignableValue(instanceValueForKey) ) {
                return false;
            }
        }
        return true;
    }
}
