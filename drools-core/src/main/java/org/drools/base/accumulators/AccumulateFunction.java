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
package org.drools.base.accumulators;

import java.io.Externalizable;

/**
 * An interface for accumulate external function implementations
 *
 * @author etirelli
 *
 */
public interface AccumulateFunction extends Externalizable {

    /**
     * Creates and returns a new context object
     * @return
     */
    public Object createContext();

    /**
     * Initializes the accumulator
     * @param context
     * @throws Exception
     */
    public void init(Object context) throws Exception;

    /**
     * Executes the accumulation action
     * @param context
     * @param value
     */
    public void accumulate(Object context,
                           Object value);

    /**
     * Reverses the accumulation action
     * @param context
     * @param value
     * @throws Exception
     */
    public void reverse(Object context,
                        Object value) throws Exception;

    /**
     * Returns the current value in this accumulation session
     *
     * @param context
     * @return
     * @throws Exception
     */
    public Object getResult(Object context) throws Exception;

    /**
     * True if the function supports reverse. False otherwise.
     *
     * @return
     */
    public boolean supportsReverse();

}