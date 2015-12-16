/*
* Copyright 2011 Red Hat, Inc. and/or its affiliates.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.drools.builder;

import org.drools.io.Resource;

/**
 * A super interface for Knowledge Building result messages.
 */
public interface KnowledgeBuilderResult {
    
    /**
     * Returns the result severity
     * @return
     */
    ResultSeverity getSeverity();
    
    /**
     * Returns the result message
     */
    String getMessage();

    /**
     * Returns the lines that generated this result message in the source file
     * @return
     */
    int[] getLines();

    /**
     * Returns the Resource that caused this result
     * @return
     */
    Resource getResource();
}
