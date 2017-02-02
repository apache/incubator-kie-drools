/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>An implementation of an accumulator capable of collecting lists of values.
 * This is similar to the "collect" CE, but allows us to collect any value, not
 * only facts.</p>
 * 
 * <p>Example:</p>
 * <pre>
 * rule "List employee names"
 * when
 *     $names : List() from accumulate(
 *             Employee( $n : firstName, $l : lastName ),
 *             collectList( $n + " " + $l ) )
 * then
 *     // do something
 * end
 * </pre>
 * 
 * <p>The list accepts duplications and the order of the elements in the list is not
 * guaranteed.</p>
 */
public class CollectListAccumulateFunction extends AbstractAccumulateFunction<CollectListAccumulateFunction.CollectListData> {

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        // functions are stateless, so nothing to serialize
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        // functions are stateless, so nothing to serialize
    }

    public static class CollectListData
        implements
        Externalizable {
        public List< Object > list = new ArrayList<Object>();

        public CollectListData() {
        }

        @SuppressWarnings("unchecked")
        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            list = (List< Object >) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( list );
        }

    }

    /* (non-Javadoc)
     * @see org.kie.base.accumulators.AccumulateFunction#createContext()
     */
    public CollectListData createContext() {
        return new CollectListData();
    }

    /* (non-Javadoc)
     * @see org.kie.base.accumulators.AccumulateFunction#init(java.lang.Object)
     */
    public void init(CollectListData data) {
        data.list.clear();
    }

    /* (non-Javadoc)
     * @see org.kie.base.accumulators.AccumulateFunction#accumulate(java.lang.Object, java.lang.Object)
     */
    public void accumulate(CollectListData data,
                           Object value) {
        data.list.add( value );
    }

    /* (non-Javadoc)
     * @see org.kie.base.accumulators.AccumulateFunction#reverse(java.lang.Object, java.lang.Object)
     */
    public void reverse(CollectListData data,
                        Object value) {
        data.list.remove( value );
    }

    /* (non-Javadoc)
     * @see org.kie.base.accumulators.AccumulateFunction#getResult(java.lang.Object)
     */
    public Object getResult(CollectListData data) {
        return Collections.unmodifiableList( data.list );
    }

    /* (non-Javadoc)
     * @see org.kie.base.accumulators.AccumulateFunction#supportsReverse()
     */
    public boolean supportsReverse() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public Class< ? > getResultType() {
        return List.class;
    }

}
