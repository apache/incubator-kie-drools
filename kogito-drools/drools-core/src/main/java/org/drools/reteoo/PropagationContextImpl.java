package org.drools.reteoo;

import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.PropagationContext;

public class PropagationContextImpl
    implements
    PropagationContext {
    private int        type;

    private Rule       rule;

    private Activation activation;

    public PropagationContextImpl(int type,
                                  Rule rule,
                                  Activation activation){
        this.type = type;
        this.rule = rule;
        this.activation = activation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.reteoo.PropagationContext#getRuleOrigin()
     */
    public Rule getRuleOrigin(){
        return this.rule;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.reteoo.PropagationContext#getActivationOrigin()
     */
    public Activation getActivationOrigin(){
        return this.activation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.reteoo.PropagationContext#getType()
     */
    public int getType(){
        return this.type;
    }

}
