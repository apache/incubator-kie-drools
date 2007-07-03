/*
 * Copyright 2007 JBoss Inc
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
 *
 * Created on Jun 20, 2007
 */
package org.drools.base.mvel;

import java.io.Serializable;
import java.util.HashMap;

import org.drools.WorkingMemory;
import org.drools.common.InternalFactHandle;
import org.drools.rule.Declaration;
import org.drools.spi.Accumulator;
import org.drools.spi.Tuple;
import org.mvel.MVEL;

/**
 * An MVEL accumulator implementation
 * 
 * @author etirelli
 */
public class MVELAccumulator
    implements
    Accumulator {

    private static final long       serialVersionUID = 400L;

    private final DroolsMVELFactory factory;
    private final Serializable      init;
    private final Serializable      action;
    private final Serializable      reverse;
    private final Serializable      result;

    public MVELAccumulator(final DroolsMVELFactory factory,
                           final Serializable init,
                           final Serializable action,
                           final Serializable reverse,
                           final Serializable result) {
        super();
        this.factory = factory;
        this.init = init;
        this.action = action;
        this.reverse = reverse;
        this.result = result;
    }

    /* (non-Javadoc)
     * @see org.drools.spi.Accumulator#createContext()
     */
    public Object createContext() {
        return new HashMap();
    }

    /* (non-Javadoc)
     * @see org.drools.spi.Accumulator#init(java.lang.Object, org.drools.spi.Tuple, org.drools.rule.Declaration[], org.drools.WorkingMemory)
     */
    public void init(Object context,
                     Tuple leftTuple,
                     Declaration[] declarations,
                     WorkingMemory workingMemory) throws Exception {
        this.factory.setContext( leftTuple,
                                 null,
                                 null,
                                 workingMemory );
        MVEL.executeExpression( this.init,
                                context,
                                this.factory );
    }

    /* (non-Javadoc)
     * @see org.drools.spi.Accumulator#accumulate(java.lang.Object, org.drools.spi.Tuple, org.drools.common.InternalFactHandle, org.drools.rule.Declaration[], org.drools.rule.Declaration[], org.drools.WorkingMemory)
     */
    public void accumulate(Object context,
                           Tuple leftTuple,
                           InternalFactHandle handle,
                           Declaration[] declarations,
                           Declaration[] innerDeclarations,
                           WorkingMemory workingMemory) throws Exception {
        this.factory.setContext( leftTuple,
                                 null,
                                 handle.getObject(),
                                 workingMemory );
        MVEL.executeExpression( this.action,
                                context,
                                this.factory );
    }

    public void reverse(Object context,
                        Tuple leftTuple,
                        InternalFactHandle handle,
                        Declaration[] declarations,
                        Declaration[] innerDeclarations,
                        WorkingMemory workingMemory) throws Exception {
        this.factory.setContext( leftTuple,
                                 null,
                                 handle.getObject(),
                                 workingMemory );
        MVEL.executeExpression( this.reverse,
                                context,
                                this.factory );
    }

    /* (non-Javadoc)
     * @see org.drools.spi.Accumulator#getResult(java.lang.Object, org.drools.spi.Tuple, org.drools.rule.Declaration[], org.drools.WorkingMemory)
     */
    public Object getResult(Object context,
                            Tuple leftTuple,
                            Declaration[] declarations,
                            WorkingMemory workingMemory) throws Exception {
        this.factory.setContext( leftTuple,
                                 null,
                                 null,
                                 workingMemory );
        final Object result = MVEL.executeExpression( this.result,
                                                      context,
                                                      this.factory );
        return result;
    }

    public boolean supportsReverse() {
        return this.reverse != null ;
    }

}
