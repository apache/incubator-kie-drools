package org.kie.api.definition.type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface Expires {

    String value();

    Policy policy() default Policy.TIME_HARD;

    enum Policy { TIME_HARD, TIME_SOFT }
}
