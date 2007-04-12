package org.objenesis.instantiator.basic;

/**
 * Instantiates a class by grabbing the no-args constructor, making it accessible and then calling
 * Constructor.newInstance(). Although this still requires no-arg constructors, it can call
 * non-public constructors (if the security manager allows it).
 * 
 * @see org.objenesis.instantiator.ObjectInstantiator
 */
public class AccessibleInstantiator extends ConstructorInstantiator {

    public AccessibleInstantiator(final Class type) {
        super( type );
        if ( this.constructor != null ) {
            this.constructor.setAccessible( true );
        }
    }
}
