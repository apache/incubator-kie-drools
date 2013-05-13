package org.drools.impl.adapters;

import org.kie.api.definition.type.Annotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AnnotationAdapter implements org.drools.definition.type.Annotation {

    private final Annotation delegate;

    public AnnotationAdapter(Annotation delegate) {
        this.delegate = delegate;
    }

    public String getName() {
        return delegate.getName();
    }

    public Object getPropertyValue(String key) {
        return delegate.getPropertyValue(key);
    }

    public Class getPropertyType(String key) {
        return delegate.getPropertyType(key);
    }

    public static List<org.drools.definition.type.Annotation> adaptAnnotations(Collection<org.kie.api.definition.type.Annotation> annotations) {
        List<org.drools.definition.type.Annotation> result = new ArrayList<org.drools.definition.type.Annotation>();
        for (org.kie.api.definition.type.Annotation annotation : annotations) {
            result.add(new AnnotationAdapter(annotation));
        }
        return result;
    }
}
