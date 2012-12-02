package org.drools.definition.type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Annotation used to declare the Position of a field, when using Positional field constraints.
 * 
 * Works for concrete classes on fields. Methods on classes or interfaces is not yet supported.
 *
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface Position {
 
    int value() default -1;
 
}