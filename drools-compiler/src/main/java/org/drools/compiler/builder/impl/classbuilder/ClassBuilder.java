package org.drools.compiler.builder.impl.classbuilder;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.drools.base.factmodel.ClassDefinition;
import org.kie.api.internal.utils.KieService;

public interface ClassBuilder extends KieService {

    byte[] buildClass(ClassDefinition def, ClassLoader classLoader ) throws IOException,
            SecurityException,
            IllegalArgumentException,
            ClassNotFoundException,
            NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException,
            NoSuchFieldException;

}
