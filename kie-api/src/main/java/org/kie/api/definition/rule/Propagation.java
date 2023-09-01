package org.kie.api.definition.rule;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface Propagation {

    public enum Type { IMMEDIATE, EAGER, LAZY }

    Type value();
}
