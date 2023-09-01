package org.drools.compiler.kie.util;

import org.kie.api.builder.model.QualifierModel;

public interface BeanCreator {
    <T> T createBean(ClassLoader cl, String type, QualifierModel qualifier ) throws Exception;
}
