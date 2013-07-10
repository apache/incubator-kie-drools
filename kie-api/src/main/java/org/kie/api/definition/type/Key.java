package org.kie.api.definition.type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * Annotation used to declare that a field is a key for the class it belongs to and then
 * it will be used to calculate the equals/hashCode methods of the class itself.
 *
 * Works for concrete classes on fields. Methods on classes or interfaces is not yet supported.
 *
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface Key {
}
