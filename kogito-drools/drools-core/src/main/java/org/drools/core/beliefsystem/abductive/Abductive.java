package org.drools.core.beliefsystem.abductive;


public @interface Abductive {

    Class target();

    String mode() default "";

}
