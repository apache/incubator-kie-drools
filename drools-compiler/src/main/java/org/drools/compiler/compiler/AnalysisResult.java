/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.compiler;

import java.util.Set;

/**
 * An interface with the results from the expression/block analysis
 */
public interface AnalysisResult {

    /**
     * Returns the Set<String> of all used identifiers
     * 
     * @return
     */
    Set<String> getIdentifiers();

    /**
     * Returns the array of lists<String> of bound identifiers
     * 
     * @return
     */
    BoundIdentifiers getBoundIdentifiers();

    /**
     * Returns the Set<String> of not bounded identifiers
     * 
     * @return
     */
    Set<String> getNotBoundedIdentifiers();

    /**
     * Returns the Set<String> of declared local variables
     * 
     * @return
     */
    Set<String> getLocalVariables();

    Class<?> getReturnType();
}
