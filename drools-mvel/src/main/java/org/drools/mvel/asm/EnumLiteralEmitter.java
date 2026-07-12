/*
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
package org.drools.mvel.asm;

import java.lang.reflect.Field;

import org.drools.compiler.builder.impl.classbuilder.BuildUtils;
import org.mvel2.asm.MethodVisitor;
import org.mvel2.asm.Opcodes;

/**
 * Compile-time detection and ASM emission of fully-qualified enum-constant
 * references in declared-type field initializers and declared-enum constructor
 * arguments.
 */
final class EnumLiteralEmitter {

    private EnumLiteralEmitter() { }

    /**
     * If {@code expression} is a reference to an enum constant whose declaring
     * class equals {@code expectedTypeName}, emit a direct {@code GETSTATIC}
     * on {@code mv} and return {@code true}. Both fully-qualified
     * ({@code "com.example.Category.TARGET"}) and simple
     * ({@code "Category.TARGET"}) forms are accepted; the simple form is
     * unambiguous because {@code expectedTypeName} has already been resolved
     * by the type resolver against the DRL's imports and same-package types.
     * Returns {@code false} for primitive targets, references that don't
     * match the expected type's name, or anything that doesn't resolve to an
     * enum constant via {@code classLoader}.
     */
    static boolean tryEmit(MethodVisitor mv,
                           String expression,
                           String expectedTypeName,
                           ClassLoader classLoader) {
        if (expression == null || expectedTypeName == null) {
            return false;
        }
        if (BuildUtils.isPrimitive(expectedTypeName) || expectedTypeName.indexOf('.') < 0) {
            return false;
        }
        String trimmed = expression.trim();
        String constantName = stripExpectedTypePrefix(trimmed, expectedTypeName);
        if (constantName == null || constantName.isEmpty() || !isJavaIdentifier(constantName)) {
            return false;
        }
        Class<?> argClass;
        try {
            argClass = Class.forName(expectedTypeName, false, classLoader);
        } catch (ClassNotFoundException | LinkageError e) {
            return false;
        }
        if (!argClass.isEnum()) {
            return false;
        }
        // From this point on, the user has clearly written the argument as an
        // enum-constant reference of the expected enum type. Any failure is a
        // user-facing error: surface it at compile time instead of letting
        // MVEL.eval fail mysteriously at class load.
        Field field;
        try {
            field = argClass.getField(constantName);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(
                "Enum constant '" + constantName + "' does not exist on '" + expectedTypeName
                    + "'. Available constants: " + listEnumConstantNames(argClass));
        }
        if (!field.isEnumConstant()) {
            throw new IllegalStateException(
                "Field '" + constantName + "' on '" + expectedTypeName
                    + "' is not an enum constant.");
        }
        mv.visitFieldInsn(Opcodes.GETSTATIC,
                          BuildUtils.getInternalType(expectedTypeName),
                          constantName,
                          BuildUtils.getTypeDescriptor(expectedTypeName));
        return true;
    }

    private static String listEnumConstantNames(Class<?> enumClass) {
        Object[] constants = enumClass.getEnumConstants();
        if (constants == null) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < constants.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(((Enum<?>) constants[i]).name());
        }
        return sb.append("]").toString();
    }

    /**
     * Returns the constant name suffix if {@code expression} starts with either
     * the FQN of {@code expectedTypeName} followed by a dot, or with the
     * expected type's simple name followed by a dot. Returns {@code null}
     * otherwise. The FQN form is tried first so a fully-qualified reference
     * always wins when the simple name happens to be a prefix of something
     * else.
     */
    private static String stripExpectedTypePrefix(String expression, String expectedTypeName) {
        String fqnPrefix = expectedTypeName + ".";
        if (expression.startsWith(fqnPrefix)) {
            return expression.substring(fqnPrefix.length());
        }
        int lastDot = expectedTypeName.lastIndexOf('.');
        if (lastDot < 0) {
            return null;
        }
        String simplePrefix = expectedTypeName.substring(lastDot + 1) + ".";
        if (expression.startsWith(simplePrefix)) {
            return expression.substring(simplePrefix.length());
        }
        return null;
    }

    private static boolean isJavaIdentifier(String s) {
        if (s.isEmpty() || !Character.isJavaIdentifierStart(s.charAt(0))) {
            return false;
        }
        for (int i = 1; i < s.length(); i++) {
            if (!Character.isJavaIdentifierPart(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
