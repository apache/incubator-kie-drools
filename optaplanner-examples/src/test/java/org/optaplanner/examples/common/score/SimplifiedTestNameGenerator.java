package org.optaplanner.examples.common.score;

import java.lang.reflect.Method;

import org.junit.jupiter.api.DisplayNameGenerator;

/**
 * Prints test names without the arguments.
 */
final class SimplifiedTestNameGenerator implements DisplayNameGenerator {

    private static final DisplayNameGenerator PARENT =
            DisplayNameGenerator.getDisplayNameGenerator(Simple.class);

    @Override
    public String generateDisplayNameForClass(Class<?> testClass) {
        return PARENT.generateDisplayNameForClass(testClass);
    }

    @Override
    public String generateDisplayNameForNestedClass(Class<?> nestedClass) {
        return PARENT.generateDisplayNameForNestedClass(nestedClass);
    }

    @Override
    public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
        return testMethod.getName();
    }
}
