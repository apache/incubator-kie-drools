package org.drools.reteoo;
/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.PropagationContext;

public class PropagationContextImpl
    implements
    PropagationContext {
    private final int        type;

    private final Rule       rule;

    private final Activation activation;
    
    private final long       propagationNumber;

    public PropagationContextImpl(long number,
                                  int type,
                                  Rule rule,
                                  Activation activation) {
        this.type = type;
        this.rule = rule;
        this.activation = activation;
        this.propagationNumber = number;
    }
    
    public long getPropagationNumber() {
        return this.propagationNumber;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.reteoo.PropagationContext#getRuleOrigin()
     */
    public Rule getRuleOrigin() {
        return this.rule;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.reteoo.PropagationContext#getActivationOrigin()
     */
    public Activation getActivationOrigin() {
        return this.activation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.reteoo.PropagationContext#getType()
     */
    public int getType() {
        return this.type;
    }

}
