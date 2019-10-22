package org.drools.mvelcompiler.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.drools.mvelcompiler.MvelCompilerException;

import static java.util.stream.Stream.of;

public class CollectionUtils {

    public static boolean isCollection(Type t) {
        return of(List.class, Map.class).anyMatch(cls -> {
            Class<?> clazz;
            if(t instanceof Class<?>) {
                clazz = (Class<?>) t;
            } else if(t instanceof ParameterizedType) {
                clazz = (Class<?>) ((ParameterizedType)t).getRawType();
            } else {
                throw new MvelCompilerException("Unable to parse type");
            }
            return cls.isAssignableFrom(clazz);
        });
    }
}
