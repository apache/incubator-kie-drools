/*
 * Copyright 2005 JBoss Inc
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

package org.drools.modelcompiler.util;

import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toClassOrInterfaceType;

public class JavaParserUtil {

    public static Type toJavaParserType(Class<?> cls) {
        return toJavaParserType( cls, cls.isPrimitive() );
    }

    public static Type toJavaParserType(Class<?> cls, boolean primitive) {
        if (primitive) {
            if (cls == int.class || cls == Integer.class) {
                return PrimitiveType.intType();
            }
            else if (cls == char.class || cls == Character.class) {
                return PrimitiveType.intType();
            }
            else if (cls == long.class || cls == Long.class) {
                return PrimitiveType.longType();
            }
            else if (cls == short.class || cls == Short.class) {
                return PrimitiveType.shortType();
            }
            else if (cls == double.class || cls == Double.class) {
                return PrimitiveType.doubleType();
            }
            else if (cls == float.class || cls == Float.class) {
                return PrimitiveType.floatType();
            }
            else if (cls == boolean.class || cls == Boolean.class) {
                return PrimitiveType.booleanType();
            }
            else if (cls == byte.class || cls == Byte.class) {
                return PrimitiveType.byteType();
            }
        }
        return toClassOrInterfaceType(cls);
    }
}
