package org.drools.compiler.rule.builder.util;

import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.core.base.TypeResolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static java.lang.reflect.Proxy.newProxyInstance;
import static org.drools.core.util.StringUtils.ucFirst;

public class AnnotationFactory {

    public static Annotation buildAnnotation(TypeResolver typeResolver, AnnotationDescr annotationDescr) {
        try {
            Class<?> annotationClass = typeResolver.resolveType(annotationDescr.getFullyQualifiedName());
            return buildAnnotation(annotationDescr, annotationClass);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static Annotation buildAnnotation(AnnotationDescr annotationDescr, Class<?> annotationClass) {
        return (Annotation) newProxyInstance(annotationClass.getClassLoader(),
                                             new Class<?>[]{Annotation.class, annotationClass},
                                             new AnnotationInvocationHandler(annotationClass, annotationDescr));
    }

    public static class AnnotationInvocationHandler implements InvocationHandler {

        private final Class<?> annotationClass;
        private final AnnotationDescr annotationDescr;

        public AnnotationInvocationHandler(Class<?> annotationClass, AnnotationDescr annotationDescr) {
            this.annotationClass = annotationClass;
            this.annotationDescr = annotationDescr;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("annotationType")) {
                return annotationClass;
            }
            if (method.getName().equals("toString")) {
                return toString();
            }
            if (method.getName().equals("hashCode")) {
                return hashCode();
            }
            if (method.getName().equals("equals")) {
                return annotationDescr.equals(args[0]);
            }
            try {
                annotationClass.getMethod(method.getName());
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Invoked not existing method " + method.getName() + " on annotation " + annotationClass.getName());
            }
            Object value = annotationDescr.getValue(method.getName());
            return value == null ? method.getDefaultValue() : normalizeResult(method.getReturnType(), value);
        }

        @Override
        public int hashCode() {
            return annotationDescr.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof AnnotationInvocationHandler &&
                   annotationDescr.equals(((AnnotationInvocationHandler)obj).annotationDescr);
        }

        @Override
        public String toString() {
            return annotationDescr.toString();
        }

        private Object normalizeResult(Class<?> resultClass, Object val) {
            String value = val.toString();
            if ( resultClass == String.class ) {
                if ( annotationDescr.isStrict() ) {
                    // quotes on a String value of a strict annotation are required
                    if (value.charAt(0) == '"' && value.charAt(value.length()-1) == '"') {
                        return value.substring(1, value.length() - 1);
                    }
                } else {
                    return value;
                }
            }
            if (resultClass == Boolean.class || resultClass == boolean.class) {
                return Boolean.valueOf(value);
            }
            if (resultClass == Integer.class || resultClass == int.class) {
                return Integer.valueOf(value);
            }
            if (resultClass.isEnum()) {
                String annotationHead = resultClass.getSimpleName() + ".";
                int typePos = value.indexOf(annotationHead);
                if (typePos >= 0) {
                    value = value.substring(typePos+annotationHead.length());
                }
                try {
                    return Enum.valueOf((Class<Enum>) resultClass, value);
                } catch (IllegalArgumentException e) {
                    if ( !annotationDescr.isStrict() ) {
                        value = ucFirst(value);
                        try {
                            return Enum.valueOf((Class<Enum>) resultClass, value);
                        } catch (IllegalArgumentException e2) {
                            return Enum.valueOf((Class<Enum>) resultClass, value.toUpperCase());
                        }
                    }
                    throw e;
                }
            }
            throw new RuntimeException("Cannot convert " + value + " to an instance of type " + resultClass.getName());
        }
    }
}
