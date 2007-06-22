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
 * Created on Jun 21, 2007
 */
package org.drools.base.accumulators;


/**
 * An implementation of an accumulator capable of calculating minimun values
 * 
 * @author etirelli
 *
 */
public class MinAccumulateFunction implements AccumulateFunction {

    protected static class MaxData {
        public double min = Double.MAX_VALUE;
    }

    /* (non-Javadoc)
     * @see org.drools.base.accumulators.AccumulateFunction#createContext()
     */
    public Object createContext() {
        return new MaxData();
    }

    /* (non-Javadoc)
     * @see org.drools.base.accumulators.AccumulateFunction#init(java.lang.Object)
     */
    public void init(Object context) throws Exception {
        MaxData data = (MaxData) context;
        data.min = Double.MAX_VALUE;
    }

    /* (non-Javadoc)
     * @see org.drools.base.accumulators.AccumulateFunction#accumulate(java.lang.Object, java.lang.Object)
     */
    public void accumulate(Object context,
                           Object value) {
        MaxData data = (MaxData) context;
        data.min = Math.min( data.min, ((Number)value).doubleValue() );
    }

    /* (non-Javadoc)
     * @see org.drools.base.accumulators.AccumulateFunction#reverse(java.lang.Object, java.lang.Object)
     */
    public void reverse(Object context,
                        Object value) throws Exception {
    }

    /* (non-Javadoc)
     * @see org.drools.base.accumulators.AccumulateFunction#getResult(java.lang.Object)
     */
    public Object getResult(Object context) throws Exception {
        MaxData data = (MaxData) context;
        return new Double( data.min );
    }

    /* (non-Javadoc)
     * @see org.drools.base.accumulators.AccumulateFunction#supportsReverse()
     */
    public boolean supportsReverse() {
        return false;
    }

}
