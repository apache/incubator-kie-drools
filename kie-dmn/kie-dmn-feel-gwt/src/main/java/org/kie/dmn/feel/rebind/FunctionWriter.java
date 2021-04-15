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
package org.kie.dmn.feel.rebind;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.feel.runtime.FEELFunction;

public class FunctionWriter {

    private final StringBuilder template;
    private final FEELFunction feelFunction;
    private boolean hasArray = false;
    private boolean hasList = false;

    public FunctionWriter(final StringBuilder template,
                          final FEELFunction feelFunction) {
        this.template = template;
        this.feelFunction = feelFunction;
    }

    public void makeFunctionTemplate() {
        template.append(String.format("if (obj instanceof %s) {\n", feelFunction.getClass().getName()));

        for (final Method declaredMethod : getInvokeMethods(feelFunction)) {

            final Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
            template.append("   if (");
            template.append(String.format("args.length == %d", parameterTypes.length));
            for (int i = 0; i < parameterTypes.length; i++) {
                if (parameterTypes[i].isArray()) {
                    template.append(" && args[" + i + "].getClass().isArray()");
                } else {
                    template.append(" && args[" + i + "] instanceof " + parameterTypes[i].getName());
                }
            }

            template.append(") {\n");
            template.append("       return ((" + feelFunction.getClass().getName() + ") obj).invoke(");
            for (int i = 0; i < parameterTypes.length; i++) {

                template.append("(" + getSafeName(parameterTypes[i]) + ") args[" + i + "] ");
                if (i < parameterTypes.length - 1) {
                    template.append(", ");
                }
            }

            template.append(");\n");
            template.append("   }\n");
        }

        if (hasArray && hasList) {

            template.append("   if (args.length == 1 && args[0].getClass().isArray()) {\n");
            template.append("       Object[] var = (Object[]) args[0];\n");
            template.append("       if (var[0] instanceof java.util.List) {\n");
            template.append("           return ((" + feelFunction.getClass().getName() + ") obj).invoke((java.util.List) var[0]);\n");
            template.append("       } else {\n");
            template.append("           return ((" + feelFunction.getClass().getName() + ") obj).invoke(var);\n");
            template.append("       }\n");
            template.append("   }\n");
        } else if (hasList) {
            template.append("   return ((" + feelFunction.getClass().getName() + ") obj).invoke((java.util.List) args[0]);\n");
        } else if (hasArray) {
            template.append("   return ((" + feelFunction.getClass().getName() + ") obj).invoke(args);\n");
        }
        template.append("}\n");
    }

    private String getSafeName(final Class<?> parameterType) {
        if (parameterType.isArray()) {
            return parameterType.getComponentType().getName() + "[]";
        } else {
            return parameterType.getName();
        }
    }

    private Method[] getInvokeMethods(final FEELFunction feelFunction) {
        final ArrayList<Method> result = new ArrayList();

        for (final Method declaredMethod : feelFunction.getClass().getDeclaredMethods()) {
            if (Modifier.isPublic(declaredMethod.getModifiers()) && declaredMethod.getName().equals("invoke")) {

                if (declaredMethod.getParameterTypes().length == 1
                        && declaredMethod.getParameterTypes()[0].isAssignableFrom(List.class)) {
                    hasList = true;
                } else if (declaredMethod.getParameterTypes().length == 1
                        && declaredMethod.getParameterTypes()[0].isArray()) {
                    hasArray = true;
                } else {
                    result.add(declaredMethod);
                }
            }
        }

        return result.toArray(new Method[result.size()]);
    }
}
