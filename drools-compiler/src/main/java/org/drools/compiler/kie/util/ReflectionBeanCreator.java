package org.drools.compiler.kie.util;

import org.kie.api.builder.model.QualifierModel;

public class ReflectionBeanCreator implements BeanCreator {

    @Override
    public <T> T createBean(ClassLoader cl, String type, QualifierModel qualifier ) throws Exception {
        if (qualifier != null) {
            throw new IllegalArgumentException("Cannot use a qualifier without a CDI container");
        }
        return (T)Class.forName(type, true, cl).newInstance();
    }
}
