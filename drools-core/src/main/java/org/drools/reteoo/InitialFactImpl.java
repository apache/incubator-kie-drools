package org.drools.reteoo;

import org.drools.InitialFact;

/**
 * We dont want users to be able to instantiate InitialFact so we expose it as
 * an interface and make the class and its constructor package protected
 * 
 * @author mproctor
 * 
 */
class InitialFactImpl
    implements
    InitialFact {
    private static final InitialFact INSTANCE = new InitialFactImpl();

    public static InitialFact getInstance() {
        return InitialFactImpl.INSTANCE;
    }

    private InitialFactImpl() {
    }
}
