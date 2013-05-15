package org.kie.api.definition.type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * Annotation used to declare if a given class is a plain fact or an event
 *
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface Role {

    Type value() default Type.FACT;

    public enum Type { FACT, EVENT }
}
