package org.drools.tms.beliefsystem.abductive;


public @interface Abductive {

    Class target();

    String mode() default "";

    String[] args() default {};
}
