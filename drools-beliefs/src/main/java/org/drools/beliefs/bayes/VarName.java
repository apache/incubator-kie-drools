package org.drools.beliefs.bayes;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Qualifier
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface VarName {
    String value();
}
