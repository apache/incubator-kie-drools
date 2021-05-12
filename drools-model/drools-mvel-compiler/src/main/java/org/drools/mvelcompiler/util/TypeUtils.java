/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mvelcompiler.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.github.javaparser.StaticJavaParser;
import org.drools.mvelcompiler.MvelCompilerException;

import static java.util.stream.Stream.of;

public class TypeUtils {

    private TypeUtils() {

    }

    public static boolean isCollection(Type t) {
        return of(List.class, Map.class).anyMatch(cls -> {
            Class<?> clazz = classFromType(t);
            return cls.isAssignableFrom(clazz);
        });
    }

    public static Class<?> classFromType(Type t) {
        Class<?> clazz;
        if(t instanceof Class<?>) {
            clazz = (Class<?>) t;
        } else if(t instanceof ParameterizedType) {
            clazz = (Class<?>) ((ParameterizedType)t).getRawType();
        } else {
            throw new MvelCompilerException("Unable to parse type");
        }
        return clazz;
    }

    public static com.github.javaparser.ast.type.Type toJPType(Type t) {
        return toJPType(classFromType(t));
    }

    public static com.github.javaparser.ast.type.Type toJPType(Class<?> c) {
        return StaticJavaParser.parseType(c.getCanonicalName());
    }
}
