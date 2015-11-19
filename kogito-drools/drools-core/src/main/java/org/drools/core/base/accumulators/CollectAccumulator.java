/*
 * Copyright 2010 JBoss Inc
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

package org.drools.core.base.accumulators;

import org.drools.core.WorkingMemory;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.rule.Collect;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Accumulator;
import org.drools.core.spi.Tuple;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Collection;

/**
 * An accumulator to execute "collect" CEs
 */
public class CollectAccumulator
    implements
    Accumulator,
    Externalizable {

    private static final long                          serialVersionUID = 510l;
    private Collect collect;
    private boolean unwrapHandle;

    public CollectAccumulator() {
    }

    public CollectAccumulator( final Collect collect, 
                               boolean unwrapHandle ) {
        this.collect = collect;
        this.unwrapHandle = unwrapHandle;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.collect = (Collect) in.readObject();
        this.unwrapHandle = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( this.collect );
        out.writeBoolean( this.unwrapHandle );
    }

    /* (non-Javadoc)
     * @see org.kie.spi.Accumulator#createContext()
     */
    public Serializable createContext() {
        return new CollectContext();
    }

    /* (non-Javadoc)
     * @see org.kie.spi.Accumulator#init(java.lang.Object, org.kie.spi.Tuple, org.kie.rule.Declaration[], org.kie.WorkingMemory)
     */
    public void init(Object workingMemoryContext,
                     Object context,
                     Tuple leftTuple,
                     Declaration[] declarations,
                     WorkingMemory workingMemory) throws Exception {
        ((CollectContext) context).result = this.collect.instantiateResultObject( (InternalWorkingMemory) workingMemory );
    }

    /* (non-Javadoc)
     * @see org.kie.spi.Accumulator#accumulate(java.lang.Object, org.kie.spi.Tuple, org.kie.common.InternalFactHandle, org.kie.rule.Declaration[], org.kie.rule.Declaration[], org.kie.WorkingMemory)
     */
    public void accumulate(Object workingMemoryContext,
                           Object context,
                           Tuple leftTuple,
                           InternalFactHandle handle,
                           Declaration[] declarations,
                           Declaration[] innerDeclarations,
                           WorkingMemory workingMemory) throws Exception {
        Object value = this.unwrapHandle ? ((LeftTuple) handle.getObject()).getFactHandle().getObject() : handle.getObject();
        ((CollectContext) context).result.add( value );
    }

    public void reverse(Object workingMemoryContext,
                        Object context,
                        Tuple leftTuple,
                        InternalFactHandle handle,
                        Declaration[] declarations,
                        Declaration[] innerDeclarations,
                        WorkingMemory workingMemory) throws Exception {
        Object value = this.unwrapHandle ? ((LeftTuple) handle.getObject()).getFactHandle().getObject() : handle.getObject();
        ((CollectContext) context).result.remove( value );
    }

    /* (non-Javadoc)
     * @see org.kie.spi.Accumulator#getResult(java.lang.Object, org.kie.spi.Tuple, org.kie.rule.Declaration[], org.kie.WorkingMemory)
     */
    public Object getResult(Object workingMemoryContext,
                            Object context,
                            Tuple leftTuple,
                            Declaration[] declarations,
                            WorkingMemory workingMemory) throws Exception {
        return ((CollectContext) context).result;
    }

    public boolean supportsReverse() {
        return true;
    }

    public Object createWorkingMemoryContext() {
        // no working memory context needed
        return null;
    }

    private static class CollectContext
        implements
        Externalizable {
        public Collection<Object> result;
        
        public CollectContext() {}

        @SuppressWarnings("unchecked")
        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            result = (Collection<Object>) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( result );
        }
    }

}
