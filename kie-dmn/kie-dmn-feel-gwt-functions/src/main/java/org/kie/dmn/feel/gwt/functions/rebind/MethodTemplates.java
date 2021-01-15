package org.kie.dmn.feel.gwt.functions.rebind;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.functions.BuiltInFunctions;
import org.kie.dmn.feel.runtime.functions.DateAndTimeFunction;
import org.kie.dmn.feel.runtime.functions.DateFunction;
import org.kie.dmn.feel.runtime.functions.GetValueFunction;
import org.kie.dmn.feel.runtime.functions.ModeFunction;
import org.kie.dmn.feel.runtime.functions.ParameterName;
import org.kie.dmn.feel.runtime.functions.ReplaceFunction;
import org.kie.dmn.feel.runtime.functions.TimeFunction;

public class MethodTemplates {

    public List<String> getAll() {
        final List<String> result = new ArrayList<>();

        for (final FEELFunction function : BuiltInFunctions.getFunctions()) {
            result.addAll(getFunctionSignatures(function));
        }

        return result;
    }

    public List<String> getFunctionSignatures(final FEELFunction function) {

        final List<String> result = new ArrayList<>();

        for (Method declaredMethod : function.getClass().getDeclaredMethods()) {
            if (!Modifier.isPublic(declaredMethod.getModifiers()) || !declaredMethod.getName().equals("invoke")) {
                continue;
            }

            StringBuilder paramBuilder = new StringBuilder();
            final Annotation[][] parameterAnnotations = declaredMethod.getParameterAnnotations();

            int i = 0;
            for (Annotation[] parameterAnnotation : parameterAnnotations) {
                if (i != 0) {
                    paramBuilder.append(", ");
                }

                final Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
                paramBuilder.append(String.format("new Parameter( \"%s\", %s )",
                                                  getParameterNameAnnotation(parameterAnnotation),
                                                  getType(parameterTypes[i].getTypeName())));
                i++;
            }

            if (paramBuilder.length() == 0) {
                result.add(String.format("new FunctionOverrideVariation( %s, \"%s\" )",
                                         getReturnType(function,
                                                       declaredMethod.getGenericReturnType()),
                                         function.getName()));
            } else {

                result.add(String.format("new FunctionOverrideVariation( %s, \"%s\", %s )",
                                         getReturnType(function,
                                                       declaredMethod.getGenericReturnType()),
                                         function.getName(),
                                         paramBuilder.toString()));
            }
        }

        return result;
    }

    protected String getParameterNameAnnotation(final Annotation[] parameterAnnotation) {
        for (Annotation annotation : parameterAnnotation) {
            if (annotation instanceof ParameterName) {
                return ((ParameterName) annotation).value();
            }
        }
        return "";
    }

    private String getReturnType(final FEELFunction function,
                                 final Type genericReturnType) {

        if (genericReturnType instanceof ParameterizedType) {
            final Type[] actualTypeArguments = ((ParameterizedType) genericReturnType).getActualTypeArguments();
            if (actualTypeArguments.length == 1) {

                /*
                 * Getting dates by function since the argument type is the same for them all.
                 * Other than that, better to use the type name so there is better support
                 * for new functions we do not need to write an if for each function.
                 */
                if (function instanceof DateFunction) {
                    return "BuiltInType.DATE";
                } else if (function instanceof TimeFunction) {
                    return "BuiltInType.TIME";
                } else if (function instanceof DateAndTimeFunction) {
                    return "BuiltInType.DATE_TIME";
                } else if (function instanceof GetValueFunction) {
                    return "BuiltInType.UNKNOWN";
                } else if (function instanceof ModeFunction) {
                    return "BuiltInType.LIST";
                } else if (function instanceof ReplaceFunction) {
                    return "BuiltInType.STRING";
                }

                final String x = getType(actualTypeArguments[0].getTypeName());
                if (x != null) {
                    return x;
                }
            }
        }

        return "BuiltInType.UNKNOWN";
    }

    private String getType(final String typeName) {
        if (typeName.equals("java.time.temporal.TemporalAmount")) {
            return "BuiltInType.DURATION";
        } else if (typeName.equals("java.lang.String")) {
            return "BuiltInType.STRING";
        } else if (typeName.equals("java.lang.Boolean")) {
            return "BuiltInType.BOOLEAN";
        } else if (typeName.startsWith("java.util.List")) {
            return "BuiltInType.LIST";
        } else if (typeName.startsWith("java.time.Duration")) {
            return "BuiltInType.DURATION";
        } else if (typeName.equals("java.math.BigDecimal")) {
            return "BuiltInType.NUMBER";
        }
        return "BuiltInType.UNKNOWN";
    }
}
