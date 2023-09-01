package org.drools.base.factmodel.traits;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention( value = RetentionPolicy.RUNTIME )
@Target( value = ElementType.TYPE )
public @interface Traitable {

    boolean logical() default false;

}
