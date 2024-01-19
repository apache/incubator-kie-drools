/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.base.accumulators;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Objects;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Collect;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.accessor.Accumulator;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.TupleImpl;
import org.kie.api.runtime.rule.FactHandle;

/**
 * An accumulator to execute "collect" CEs
 */
public class CollectAccumulator implements Accumulator, Externalizable {

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
    public Object createContext() {
        return null; // this is always instantiated in init - for now, can we fix this? (mdp)
    }

    /* (non-Javadoc)
     * @see org.kie.spi.Accumulator#init(java.lang.Object, org.kie.spi.Tuple, org.kie.rule.Declaration[], org.kie.WorkingMemory)
     */
    public Object init(Object workingMemoryContext,
                       Object context,
                       BaseTuple leftTuple,
                       Declaration[] declarations,
                       ValueResolver valueResolver) {
        return this.collect.instantiateResultObject( valueResolver );
    }

    /* (non-Javadoc)
     * @see org.kie.spi.Accumulator#accumulate(java.lang.Object, org.kie.spi.Tuple, org.kie.common.InternalFactHandle, org.kie.rule.Declaration[], org.kie.rule.Declaration[], org.kie.WorkingMemory)
     */
    public Object accumulate(Object workingMemoryContext,
                             Object context,
                             BaseTuple leftTuple,
                             FactHandle handle,
                             Declaration[] declarations,
                             Declaration[] innerDeclarations,
                             ValueResolver valueResolver) {
        Object value = this.unwrapHandle ? ((TupleImpl) handle.getObject()).getFactHandle().getObject() : handle.getObject();
        ((Collection) context).add( value );
        return value;
    }

    public boolean tryReverse(Object workingMemoryContext,
                              Object context,
                              BaseTuple leftTuple,
                              FactHandle handle,
                              Object value,
                              Declaration[] declarations,
                              Declaration[] innerDeclarations,
                              ValueResolver valueResolver) {
        ((Collection) context).remove( value );
        return true;
    }

    /* (non-Javadoc)
     * @see org.kie.spi.Accumulator#getResult(java.lang.Object, org.kie.spi.Tuple, org.kie.rule.Declaration[], org.kie.WorkingMemory)
     */
    public Object getResult(Object workingMemoryContext,
                            Object context,
                            BaseTuple leftTuple,
                            Declaration[] declarations,
                            ValueResolver valueResolver) {
        return context;
    }

    public boolean supportsReverse() {
        return true;
    }

    public Object createWorkingMemoryContext() {
        // no working memory context needed
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CollectAccumulator that = (CollectAccumulator) o;
        return unwrapHandle == that.unwrapHandle && Objects.equals(collect, that.collect);
    }

    @Override
    public int hashCode() {
        return Objects.hash(collect, unwrapHandle);
    }
}
