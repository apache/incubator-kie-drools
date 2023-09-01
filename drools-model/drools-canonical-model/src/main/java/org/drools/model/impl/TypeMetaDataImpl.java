package org.drools.model.impl;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.drools.model.AnnotationValue;
import org.drools.model.TypeMetaData;

import static java.util.Comparator.comparing;

public class TypeMetaDataImpl implements TypeMetaData, ModelComponent {

    private final Class<?> type;
    private final String pkg;
    private final String name;
    private final Map<String, AnnotationValue[]> annotations = new HashMap<>();

    public TypeMetaDataImpl( Class<?> type ) {
        this.type = type;
        this.pkg = type.getPackage().getName();
        this.name = type.getSimpleName();
    }

    @Override
    public String getPackage() {
        return pkg;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public Map<String, AnnotationValue[]> getAnnotations() {
        return annotations;
    }

    public TypeMetaDataImpl addAnnotation( String name, AnnotationValue... values) {
        annotations.put(name, values);
        return this;
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        TypeMetaDataImpl other = ( TypeMetaDataImpl ) o;

        if ( !pkg.equals( other.pkg ) ) return false;
        if ( !name.equals( other.name ) ) return false;

        Field[] thisFields = type.getDeclaredFields();
        Field[] otherFields = other.type.getDeclaredFields();

        if ( thisFields.length != otherFields.length ) return false;

        Arrays.sort( thisFields, comparing( Field::getName ) );
        Arrays.sort( otherFields, comparing( Field::getName ) );

        for ( int i = 0; i < thisFields.length; i++ ) {
            if ( ! (thisFields[i].getName().equals( otherFields[i].getName() ) && thisFields[i].getType().equals( otherFields[i].getType() )) ) {
                return false;
            }
        }

        return true;
    }
}
