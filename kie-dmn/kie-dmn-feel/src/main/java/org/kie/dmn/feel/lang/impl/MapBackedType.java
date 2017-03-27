package org.kie.dmn.feel.lang.impl;

import org.kie.dmn.feel.lang.CompositeType;
import org.kie.dmn.feel.lang.Type;

import java.util.LinkedHashMap;
import java.util.Map;

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
}
