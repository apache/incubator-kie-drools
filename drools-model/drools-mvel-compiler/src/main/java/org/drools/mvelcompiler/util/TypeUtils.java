/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvelcompiler.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import com.github.javaparser.StaticJavaParser;
import org.kie.api.prototype.PrototypeFactInstance;

import static org.drools.util.ClassUtils.classFromType;
import static org.drools.util.ClassUtils.rawType;

public class TypeUtils {

    private TypeUtils() {

    }

    public static com.github.javaparser.ast.type.Type toJPType(Type t) {
        return toJPType(classFromType(t));
    }

    public static com.github.javaparser.ast.type.Type toJPType(Class<?> c) {
        return StaticJavaParser.parseType(c.getCanonicalName());
    }

    public static boolean isMapAccessField(Type type) {
        if (type instanceof Class c) {
            return Map.class.isAssignableFrom(c);
        }
        if (type instanceof ParameterizedType p) {
            if (!Map.class.isAssignableFrom(rawType(p.getRawType()))) {
                return false;
            }
            Class<?> valueType = rawType(p.getActualTypeArguments()[1]);
            return valueType == Object.class || Map.class.isAssignableFrom(valueType) || PrototypeFactInstance.class.isAssignableFrom(valueType);
        }
        return false;
    }
}
