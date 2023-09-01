package org.kie.api.definition.type;

public interface Annotation {

    public String getName();

    public Object getPropertyValue( String key );

    public Class getPropertyType( String key );
}
