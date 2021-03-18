package org.kie.dmn.feel.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;

/**
 * For internal use for the scope of https://github.com/jacoco/jacoco/issues/731
 * and coverage analysis.
 * Similar to {@link javax.annotation.Generated}, but with RetentionPolicy.CLASS
 */
@Retention(RetentionPolicy.CLASS)
@Target({PACKAGE, TYPE, ANNOTATION_TYPE, METHOD, CONSTRUCTOR, FIELD,
         LOCAL_VARIABLE, PARAMETER})
public @interface Generated {
    
    /**
     * The value element MUST have a reference to the used code generator.
     */
    String[] value();
}