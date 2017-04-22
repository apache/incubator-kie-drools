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
        fields.putAll( fields );
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
        Map<String, Object> instance = null;
        try {
            instance = (Map<String, Object>) o;
        } catch ( Exception e ) {
            return false;
        }
        for ( Entry<String, Type> f : fields.entrySet() ) {
            if ( instance.get(f.getKey()) == null ) {
                return false;
            } else {
                if ( !f.getValue().isInstanceOf(instance.get(f.getKey())) ) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean isAssignableValue(Object value) {
        if ( value == null ) {
            return true;
        }
        Map<String, Object> instance = null;
        try {
            instance = (Map<String, Object>) value;
        } catch ( Exception e ) {
            return false;
        }
        for ( Entry<String, Type> f : fields.entrySet() ) {
            if ( instance.get(f.getKey()) == null ) {
                return false;
            } else {
                if ( !f.getValue().isAssignableValue(instance.get(f.getKey())) ) {
                    return false;
                }
            }
        }
        return true;
    }
}
