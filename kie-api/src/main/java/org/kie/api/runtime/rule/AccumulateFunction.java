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
package org.kie.api.runtime.rule;

import java.io.Externalizable;
import java.io.Serializable;

/**
 * An interface for accumulate external function implementations
 */
public interface AccumulateFunction<C extends Serializable> extends Externalizable {

    /**
     * Creates and returns a new context object
     * @return new context object
     */
    C createContext();

    /**
     * Initializes the accumulator
     * @param context never null
     * @throws Exception
     */
    void init(C context) throws Exception;

    /**
     * Initializes the accumulator, possibly returning a new accumulation context instead of the original one
     * @param context
     * @return new context object
     */
    default C initContext(C context) {
        try {
            init( context );
        } catch (Exception e) {
            throw new RuntimeException( e );
        }
        return context;
    }

    /**
     * Executes the accumulation action
     * @param context never null
     * @param value
     */
    void accumulate(C context, Object value);

    /**
     * Executes the accumulation action returning the accumulated object that will be passed back
     * to the reverse method when this value will be eventually removed from acccumulation.
     * @param context
     * @param value
     * @return the accumulated object
     */
    default Object accumulateValue(C context, Object value) {
        accumulate( context, value );
        return value;
    }

    /**
     * Reverses the accumulation action
     * @param context never null
     * @param value
     * @throws Exception
     */
    void reverse(C context, Object value) throws Exception;

    /**
     * @return the current value in this accumulation session
     * @throws Exception
     */
    Object getResult(C context) throws Exception;

    /**
     * @return true if the function supports reverse, otherwise false
     */
    boolean supportsReverse();

    /**
     * @return the class type of the result of this function
     */
    Class<?> getResultType();

    /**
     * Reverses the accumulation action
     * @param context
     * @param value
     * @return true if this accumulate function was able to remove this value, false otherwise.
     *         In this last case the engine will have to retrigger a full reaccumulation.
     */
    default boolean tryReverse(C context, Object value) {
        if (supportsReverse()) {
            try {
                reverse( context, value );
            } catch (Exception e) {
                throw new RuntimeException( e );
            }
            return true;
        }
        return false;
    }
}
