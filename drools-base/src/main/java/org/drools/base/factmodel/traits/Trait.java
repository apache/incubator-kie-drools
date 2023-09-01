package org.drools.base.factmodel.traits;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target( value = ElementType.TYPE)
public @interface Trait {

    Class impl() default NullMixin.class;

    class NullMixin {
        private NullMixin() {}
    }

    boolean logical() default false;

    MixinConflictResolutionStrategy mixinSolveConflicts() default MixinConflictResolutionStrategy.DECLARATION_ORDER;

    enum MixinConflictResolutionStrategy {
        DECLARATION_ORDER, ERROR_ON_CONFLICT
    }
}
