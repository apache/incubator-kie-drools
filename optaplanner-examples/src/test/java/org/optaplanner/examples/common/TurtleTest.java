package org.optaplanner.examples.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * Turtle tests are not run by default. They are only run if {@code -DrunTurtleTests=true} because it takes days.
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Test
@Execution(ExecutionMode.CONCURRENT)
@EnabledIfSystemProperty(named = TestSystemProperties.RUN_TURTLE_TESTS, matches = "true")
public @interface TurtleTest {

}
