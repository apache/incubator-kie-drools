package org.drools.base.base;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import org.kie.internal.builder.KnowledgeBuilderResult;

public interface ClassFieldInspector {
    Map<String, Integer> getFieldNames();

    boolean isNonGetter( String name );

    Map<String, Field> getFieldTypesField();

    Map<String, Class< ? >> getFieldTypes();

    Class< ? > getFieldType(String name);

    Map<String, Method> getGetterMethods();

    Map<String, Method> getSetterMethods();

    Collection<KnowledgeBuilderResult> getInspectionResults( String fieldName );
}
