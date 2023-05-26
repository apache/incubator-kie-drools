package org.drools.core.rule;

public interface Behavior extends RuleComponent, Cloneable  {

    /**
     * Returns the type of the behavior
     */
    Behavior.BehaviorType getType();

    long getExpirationOffset();

    enum BehaviorType {
        TIME_WINDOW( "time" ),
        LENGTH_WINDOW( "length" );

        private final String id;

        BehaviorType( String id ) {
            this.id = id;
        }

        public boolean matches( String id ) {
            return this.id.equalsIgnoreCase( id );
        }
    }
}