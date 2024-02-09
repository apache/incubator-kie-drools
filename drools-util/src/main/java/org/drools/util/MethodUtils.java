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
package org.drools.util;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Optional;

public class MethodUtils {

    /* This method works better than simple Clazz.getMethod in presence of polymorphism.
     *  This returns null when no method is found */
    public static Method findMethod(Class<?> clazz, String methodName, Class[] argsType) {
        try {
            return clazz.getMethod(methodName, argsType);
        } catch (NoSuchMethodException e) {
            return getBestCandidate(clazz, argsType, methodName);
        }
    }

    public static Optional<Method> getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        return Optional.ofNullable( findMethod(clazz, name, parameterTypes) );
    }

    private static Method getBestCandidate(Class clazz, Class[] argsType, String methodName) {
        return getBestCandidate(clazz, argsType, methodName, clazz.getMethods());
    }

    private static Method getBestCandidate(Class clazz, Class[] argsType, String methodName, Method[] methods) {
        final Method bestCandidate = getBestCandidateMethod(methodName, argsType, methods, null);
        if (bestCandidate != null) {
            return bestCandidate;
        } else if (clazz.isInterface()) {
            final Method[] objMethods = Object.class.getMethods();
            final Method[] nMethods = new Method[methods.length + objMethods.length];
            System.arraycopy( methods, 0, nMethods, 0, methods.length );
            System.arraycopy( objMethods, 0, nMethods, methods.length, objMethods.length );
            return getBestCandidateMethod(methodName, argsType, nMethods, bestCandidate);
        } else {
            return null;
        }
    }

    private static Method getBestCandidateMethod(final String methodName,
                                                 final Class[] argsType,
                                                 final Method[] methods,
                                                 final Method oldBestCandidate) {
        Class<?>[] parameterTypes;
        int bestScore = -1;
        Method bestCandidate = oldBestCandidate;
        for (final Method method : methods) {
            if (methodName.equals(method.getName())) {
                parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 0 && argsType.length == 0) {
                    if (bestCandidate == null || isMoreSpecialized(method, bestCandidate) ) {
                        bestCandidate = method;
                    }
                    continue;
                }

                final boolean isVarArgs = method.isVarArgs();
                if ( isArgsNumberNotCompatible( argsType, parameterTypes, isVarArgs ) ) {
                    continue;
                }

                final int score = getMethodScore(argsType, parameterTypes, isVarArgs);
                if (score != 0) {
                    if (score > bestScore) {
                        bestCandidate = method;
                        bestScore = score;
                    } else if ((score == bestScore) && (isMoreSpecialized(method, bestCandidate) && !isVarArgs)) {
                        bestCandidate = method;
                    }
                }
            }
        }
        return bestCandidate;
    }

    private static boolean isArgsNumberNotCompatible(Class[] arguments, Class<?>[] parameterTypes, boolean isVarArgs ) {
        return ( isVarArgs && parameterTypes.length-1 > arguments.length ) || ( !isVarArgs && parameterTypes.length != arguments.length );
    }

    private static boolean isMoreSpecialized(Method newCandidate, Method oldCandidate ) {
        return oldCandidate.getReturnType().isAssignableFrom( newCandidate.getReturnType()) &&
                oldCandidate.getDeclaringClass().isAssignableFrom( newCandidate.getDeclaringClass());
    }

    private static int getMethodScore(Class[] arguments, Class<?>[] parameterTypes, boolean varArgs) {
        int score = 0;
        for (int i = 0; i != arguments.length; i++) {
            Class<?> actualParamType;
            if (varArgs && i >= parameterTypes.length - 1)
                actualParamType = parameterTypes[parameterTypes.length - 1].getComponentType();
            else
                actualParamType = parameterTypes[i];

            if (arguments[i] == null) {
                if (!actualParamType.isPrimitive()) {
                    score += 7;
                }
                else {
                    score = 0;
                    break;
                }

            } else if (actualParamType == arguments[i]) {
                score += 8;

            } else if (actualParamType.isPrimitive() && boxPrimitive(actualParamType) == arguments[i]) {
                score += 7;

            } else if (arguments[i].isPrimitive() && unboxPrimitive(arguments[i]) == actualParamType) {
                score += 7;

            } else if (actualParamType.isAssignableFrom(arguments[i])) {
                score += 6;

            } else if (isPrimitiveSubtype(arguments[i], actualParamType)) {
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
        if (score == 0 && varArgs && parameterTypes.length - 1 == arguments.length) {
            score += 3;
        }
        return score;
    }

    private static boolean canConvert(Class toType, Class convertFrom) {
        return isAssignableFrom(toType, convertFrom) ||
               (toType.isArray() && canConvert(toType.getComponentType(), convertFrom));
    }

    private static int scoreInterface(Class<?> parm, Class<?> arg) {
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

    private static boolean isPrimitiveSubtype(Class argument, Class<?> actualParamType ) {
        if (!actualParamType.isPrimitive()) {
            return false;
        }
        Class<?> primitiveArgument = unboxPrimitive(argument);
        if (!primitiveArgument.isPrimitive()) {
            return false;
        }
        return ( actualParamType == double.class && primitiveArgument == float.class ) ||
                ( actualParamType == double.class && primitiveArgument == int.class ) ||
                ( actualParamType == float.class && primitiveArgument == long.class ) ||
                ( actualParamType == long.class && primitiveArgument == int.class ) ||
                ( actualParamType == int.class && primitiveArgument == char.class ) ||
                ( actualParamType == int.class && primitiveArgument == short.class ) ||
                ( actualParamType == short.class && primitiveArgument == byte.class );
    }

    private static boolean isNumericallyCoercible(Class target, Class parameterMethod) {
        Class boxedTarget = target.isPrimitive() ? boxPrimitive(target) : target;

        if (boxedTarget != null && Number.class.isAssignableFrom(target)) {
            if ((boxedTarget = parameterMethod.isPrimitive() ? boxPrimitive(parameterMethod) : parameterMethod) != null) {
                return Number.class.isAssignableFrom(boxedTarget);
            }
        }
        return false;
    }

    public static Type boxTypePrimitive(Type type) {
        if (type instanceof Class<?>) {
            return boxPrimitive((Class<?>)type);
        } else {
            return type;
        }
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

    public static boolean areBoxingCompatible(Class<?> c1, Class<?> c2) {
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

    public static boolean isOverride(Method oldMethod, Method newMethod) {
        return oldMethod.getName().equals(newMethod.getName()) &&
               !oldMethod.getDeclaringClass().equals(newMethod.getDeclaringClass()) &&
               oldMethod.getDeclaringClass().isAssignableFrom(newMethod.getDeclaringClass());
    }

    public interface NullType { }
}
