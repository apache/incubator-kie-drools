package org.drools.spi;

import org.drools.rule.Rule;

public interface PropagationContext {

    public static final int ASSERTION    = 0;
    public static final int RETRACTION   = 1;
    public static final int MODIFICATION = 2;

    public abstract Rule getRuleOrigin();

    public abstract Activation getActivationOrigin();

    public abstract int getType();

}