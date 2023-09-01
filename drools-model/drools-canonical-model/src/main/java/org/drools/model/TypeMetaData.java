package org.drools.model;

import java.util.Map;

public interface TypeMetaData extends NamedModelItem {
    Class<?> getType();
    Map<String, AnnotationValue[]> getAnnotations();
}
