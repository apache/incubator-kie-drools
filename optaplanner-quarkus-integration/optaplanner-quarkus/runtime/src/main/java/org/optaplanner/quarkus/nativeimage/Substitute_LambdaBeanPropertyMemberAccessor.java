package org.optaplanner.quarkus.nativeimage;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

/**
 * LambdaBeanPropertyMemberAccessor works by creating a new class during runtime (via LambdaMetafactory) to delegate to
 * the provided getter/setter methods. This is not supported in GraalVM, so we need to use Method reflection
 * (i.e. {@link Method#invoke(Object, Object...)}) instead.
 */
@TargetClass(className = "org.optaplanner.core.impl.domain.common.accessor.LambdaBeanPropertyMemberAccessor")
public final class Substitute_LambdaBeanPropertyMemberAccessor {

    @Alias
    Method getterMethod;

    @Alias
    Method setterMethod;

    @Substitute
    private Function<Object, Object> createGetterFunction(MethodHandles.Lookup lookup) {
        return new GetterDelegationFunction(getterMethod);
    }

    @Substitute
    private BiConsumer<Object, Object> createSetterFunction(MethodHandles.Lookup lookup) {
        if (setterMethod == null) {
            return null;
        }

        return new SetterDelegationBiConsumer(setterMethod);
    }

    private static final class GetterDelegationFunction implements Function<Object, Object> {
        private final Method method;

        public GetterDelegationFunction(Method method) {
            this.method = method;
        }

        @Override
        public Object apply(Object object) {
            try {
                return method.invoke(object);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static final class SetterDelegationBiConsumer implements BiConsumer<Object, Object> {
        private final Method method;

        public SetterDelegationBiConsumer(Method method) {
            this.method = method;
        }

        @Override
        public void accept(Object object, Object value) {
            try {
                method.invoke(object, value);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
