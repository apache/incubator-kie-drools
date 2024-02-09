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
package org.kie.pmml.evaluator.core.executor;

import java.util.List;

/**
 * Actual implementation is required to retrieve a
 * <code>List&lt;PMMLModelEvaluator&gt;</code> out from the classes found in the classpath
 */
public interface PMMLModelEvaluatorFinder {

    /**
     * Retrieve all the <code>PMMLModelExecutor</code> implementations in the classpath
     *
     * @param refresh pass <code>true</code> to reload classes from classpath; <code>false</code> to use cached ones
     * @return
     */
    List<PMMLModelEvaluator> getImplementations(boolean refresh);
}