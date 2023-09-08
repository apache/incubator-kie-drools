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
package org.drools.base.rule.consequence;

import org.drools.base.base.ValueResolver;
import org.drools.base.rule.accessor.Invoker;

/**
 * Consequence to be fired upon successful match of a <code>Rule</code>.
 */
public interface Consequence<T extends ConsequenceContext>
    extends
        Invoker {
    
    String getName();
    
    /**
     * Execute the consequence for the supplied matching <code>Tuple</code>.
     * 
     * @param knowledgeHelper
     * @param valueResolver
     *            The working memory session.
     * @throws ConsequenceException
     *             If an error occurs while attempting to invoke the
     *             consequence.
     */
    void evaluate(T knowledgeHelper,
                  ValueResolver valueResolver) throws Exception;

}
