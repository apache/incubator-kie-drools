package org.drools.mvelcompiler.util;

import java.lang.reflect.Type;

import com.github.javaparser.StaticJavaParser;
import static org.drools.util.ClassUtils.classFromType;

public class TypeUtils {

    private TypeUtils() {

    }

    public static com.github.javaparser.ast.type.Type toJPType(Type t) {
        return toJPType(classFromType(t));
    }

    public static com.github.javaparser.ast.type.Type toJPType(Class<?> c) {
        return StaticJavaParser.parseType(c.getCanonicalName());
    }
}
