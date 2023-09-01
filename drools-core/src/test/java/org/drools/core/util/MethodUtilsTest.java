package org.drools.core.util;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

import org.drools.util.MethodUtils;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MethodUtilsTest {

    public static class MyClass {

        public static int methodInt(int a) {
            return a;
        }
    }

    @Test
    public void testFindMethod() {
        Method m = MethodUtils.findMethod(Object.class, "toString", new Class[0]);
        assertThat(m).isNotNull();
        assertThat(m.getName()).isEqualTo("toString");
        assertThat(m.getParameters()).isEmpty();
    }

    @Test
    public void testFindIntMethodWithBigDecimal() {
        Method m = MethodUtils.findMethod(MyClass.class, "methodInt", new Class[]{BigDecimal.class});
        assertThat(m).isNotNull();
        assertThat(m.getName()).isEqualTo("methodInt");
        assertThat(parametersTypeName(m.getParameters())).containsExactly("int");
    }

    @Test
    public void testFindObjectMethodWithString() {
        Method m = MethodUtils.findMethod(Map.class, "get", new Class[]{String.class});
        assertThat(m).isNotNull();
        assertThat(m.getName()).isEqualTo("get");
        assertThat(parametersTypeName(m.getParameters())).containsExactly("java.lang.Object");
    }

    private Stream<String> parametersTypeName(Parameter[] parameters) {
        return Arrays.stream(parameters).map(Parameter::getType).map(Class::getCanonicalName);
    }
}