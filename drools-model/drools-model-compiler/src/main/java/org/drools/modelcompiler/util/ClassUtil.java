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

import java.lang.reflect.Method;

public class ClassUtil {

    public static class NullType { }

    public static Method findMethod(Class<?> clazz, String methodName, Class[] argsType) {
        try {
            return clazz.getMethod(methodName, argsType);
        } catch (NoSuchMethodException e) {
            Method m = getBestCandidate(clazz, argsType, methodName);
            if (m != null) {
                return m;
            }
            throw new UnsupportedOperationException("Method " + methodName + " on class " + clazz.getName() + " is missing");
        }
    }

    public static Method getBestCandidate(Class clazz, Class[] argsType, String methodName) {
        return getBestCandidate(clazz, argsType, methodName, clazz.getMethods());
    }

    public static Method getBestCandidate(Class clazz, Class[] argsType, String methodName, Method[] methods) {
        if (methods.length == 0) {
            return null;
        }

        Class<?>[] parmTypes;
        Method bestCandidate = null;
        int bestScore = -1;
        boolean retry = false;

        do {
            for (Method meth : methods) {

                if (methodName.equals(meth.getName())) {
                    parmTypes = meth.getParameterTypes();
                    if (parmTypes.length == 0 && argsType.length == 0) {
                        if (bestCandidate == null || isMoreSpecialized(meth, bestCandidate) ) {
                            bestCandidate = meth;
                        }
                        continue;
                    }

                    boolean isVarArgs = meth.isVarArgs();
                    if ( isArgsNumberNotCompatible( argsType, parmTypes, isVarArgs ) ) {
                        continue;
                    }

                    int score = getMethodScore(argsType, parmTypes, isVarArgs);
                    if (score != 0) {
                        if (score > bestScore) {
                            bestCandidate = meth;
                            bestScore = score;
                        }
                        else if (score == bestScore) {
                            if (isMoreSpecialized(meth, bestCandidate) && !isVarArgs) {
                                bestCandidate = meth;
                            }
                        }
                    }
                }
            }

            if (bestCandidate != null) {
                break;
            }

            if (!retry && clazz.isInterface()) {
                Method[] objMethods = Object.class.getMethods();
                Method[] nMethods = new Method[methods.length + objMethods.length];
                for (int i = 0; i < methods.length; i++) {
                    nMethods[i] = methods[i];
                }

                for (int i = 0; i < objMethods.length; i++) {
                    nMethods[i + methods.length] = objMethods[i];
                }
                methods = nMethods;

                retry = true;
            }
            else {
                break;
            }
        }
        while (true);

        return bestCandidate;
    }

    private static boolean isArgsNumberNotCompatible( Class[] arguments, Class<?>[] parmTypes, boolean isVarArgs ) {
        return ( isVarArgs && parmTypes.length-1 > arguments.length ) || ( !isVarArgs && parmTypes.length != arguments.length );
    }

    private static boolean isMoreSpecialized( Method newCandidate, Method oldCandidate ) {
        return oldCandidate.getReturnType().isAssignableFrom( newCandidate.getReturnType()) &&
                oldCandidate.getDeclaringClass().isAssignableFrom( newCandidate.getDeclaringClass());
    }

    private static int getMethodScore(Class[] arguments, Class<?>[] parmTypes, boolean varArgs) {
        int score = 0;
        for (int i = 0; i != arguments.length; i++) {
            Class<?> actualParamType;
            if (varArgs && i >= parmTypes.length - 1)
                actualParamType = parmTypes[parmTypes.length - 1].getComponentType();
            else
                actualParamType = parmTypes[i];

            if (arguments[i] == null) {
                if (!actualParamType.isPrimitive()) {
                    score += 6;
                }
                else {
                    score = 0;
                    break;
                }

            } else if (actualParamType == arguments[i]) {
                score += 7;

            } else if (actualParamType.isPrimitive() && boxPrimitive(actualParamType) == arguments[i]) {
                score += 6;

            } else if (arguments[i].isPrimitive() && unboxPrimitive(arguments[i]) == actualParamType) {
                score += 6;

            } else if (actualParamType.isAssignableFrom(arguments[i])) {
                score += 5;

            } else if (isNumericallyCoercible(arguments[i], actualParamType)) {
                score += 4;

            } else if (boxPrimitive(actualParamType).isAssignableFrom(boxPrimitive(arguments[i]))
                    && Object.class != arguments[i]) {
                score += 3 + scoreInterface(actualParamType, arguments[i]);

            } else if (canConvert(actualParamType, arguments[i])) {
                if (actualParamType.isArray() && arguments[i].isArray()) {
                    score += 1;
                } else if (actualParamType == char.class && arguments[i] == String.class) {
                    score += 1;
                }

                score += 1;

            } else if (actualParamType == Object.class || arguments[i] == NullType.class) {
                score += 1;

            } else {
                score = 0;
                break;
            }
        }
        if (score == 0 && varArgs && parmTypes.length - 1 == arguments.length) {
            score += 3;
        }
        return score;
    }

    public static boolean canConvert(Class toType, Class convertFrom) {
        return isAssignableFrom(toType, convertFrom) ||
               (toType.isArray() && canConvert(toType.getComponentType(), convertFrom));
    }

    public static int scoreInterface(Class<?> parm, Class<?> arg) {
        if (parm.isInterface()) {
            Class[] iface = arg.getInterfaces();
            if (iface != null) {
                for (Class c : iface) {
                    if (c == parm) return 1;
                    else if (parm.isAssignableFrom(c)) return scoreInterface(parm, arg.getSuperclass());
                }
            }
        }
        return 0;
    }

    public static boolean isNumericallyCoercible(Class target, Class parm) {
        Class boxedTarget = target.isPrimitive() ? boxPrimitive(target) : target;

        if (boxedTarget != null && Number.class.isAssignableFrom(target)) {
            if ((boxedTarget = parm.isPrimitive() ? boxPrimitive(parm) : parm) != null) {
                return Number.class.isAssignableFrom(boxedTarget);
            }
        }
        return false;
    }

    public static Class<?> boxPrimitive(Class cls) {
        if (cls == int.class || cls == Integer.class) {
            return Integer.class;
        }
        else if (cls == int[].class || cls == Integer[].class) {
            return Integer[].class;
        }
        else if (cls == char.class || cls == Character.class) {
            return Character.class;
        }
        else if (cls == char[].class || cls == Character[].class) {
            return Character[].class;
        }
        else if (cls == long.class || cls == Long.class) {
            return Long.class;
        }
        else if (cls == long[].class || cls == Long[].class) {
            return Long[].class;
        }
        else if (cls == short.class || cls == Short.class) {
            return Short.class;
        }
        else if (cls == short[].class || cls == Short[].class) {
            return Short[].class;
        }
        else if (cls == double.class || cls == Double.class) {
            return Double.class;
        }
        else if (cls == double[].class || cls == Double[].class) {
            return Double[].class;
        }
        else if (cls == float.class || cls == Float.class) {
            return Float.class;
        }
        else if (cls == float[].class || cls == Float[].class) {
            return Float[].class;
        }
        else if (cls == boolean.class || cls == Boolean.class) {
            return Boolean.class;
        }
        else if (cls == boolean[].class || cls == Boolean[].class) {
            return Boolean[].class;
        }
        else if (cls == byte.class || cls == Byte.class) {
            return Byte.class;
        }
        else if (cls == byte[].class || cls == Byte[].class) {
            return Byte[].class;
        }

        return cls;
    }

    public static Class unboxPrimitive(Class cls) {
        if (cls == Integer.class || cls == int.class) {
            return int.class;
        }
        else if (cls == Integer[].class || cls == int[].class) {
            return int[].class;
        }
        else if (cls == Long.class || cls == long.class) {
            return long.class;
        }
        else if (cls == Long[].class || cls == long[].class) {
            return long[].class;
        }
        else if (cls == Character.class || cls == char.class) {
            return char.class;
        }
        else if (cls == Character[].class || cls == char[].class) {
            return char[].class;
        }
        else if (cls == Short.class || cls == short.class) {
            return short.class;
        }
        else if (cls == Short[].class || cls == short[].class) {
            return short[].class;
        }
        else if (cls == Double.class || cls == double.class) {
            return double.class;
        }
        else if (cls == Double[].class || cls == double[].class) {
            return double[].class;
        }
        else if (cls == Float.class || cls == float.class) {
            return float.class;
        }
        else if (cls == Float[].class || cls == float[].class) {
            return float[].class;
        }
        else if (cls == Boolean.class || cls == boolean.class) {
            return boolean.class;
        }
        else if (cls == Boolean[].class || cls == boolean[].class) {
            return boolean[].class;
        }
        else if (cls == Byte.class || cls == byte.class) {
            return byte.class;
        }
        else if (cls == Byte[].class || cls == byte[].class) {
            return byte[].class;
        }
        return cls;
    }

    public static boolean isAssignableFrom(Class<?> from, Class<?> to) {
        return from.isAssignableFrom(to) || areBoxingCompatible(from, to);
    }

    private static boolean areBoxingCompatible(Class<?> c1, Class<?> c2) {
        return c1.isPrimitive() ? isPrimitiveOf(c2, c1) : (c2.isPrimitive() && isPrimitiveOf(c1, c2));
    }

    private static boolean isPrimitiveOf(Class<?> boxed, Class<?> primitive) {
        if (primitive == int.class) return boxed == Integer.class;
        if (primitive == long.class) return boxed == Long.class;
        if (primitive == double.class) return boxed == Double.class;
        if (primitive == float.class) return boxed == Float.class;
        if (primitive == short.class) return boxed == Short.class;
        if (primitive == byte.class) return boxed == Byte.class;
        if (primitive == char.class) return boxed == Character.class;
        if (primitive == boolean.class) return boxed == Boolean.class;
        return false;
    }

    public static Class<?> toNonPrimitiveType(Class<?> c) {
        if (!c.isPrimitive()) return c;
        if (c == int.class) return Integer.class;
        if (c == long.class) return Long.class;
        if (c == double.class) return Double.class;
        if (c == float.class) return Float.class;
        if (c == short.class) return Short.class;
        if (c == byte.class) return Byte.class;
        if (c == char.class) return Character.class;
        return Boolean.class;
    }
}
