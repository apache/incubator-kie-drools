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
package org.drools.compiler.builder.impl;

import java.util.Collection;

import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.KnowledgeBuilderResults;
import org.kie.internal.builder.ResultSeverity;

/**
 * Holds build processing info, warnings and errors.
 */
public interface BuildResultCollector {
    void addBuilderResult(KnowledgeBuilderResult result);

    /**
     * This will return true if there were errors in the package building and
     * compiling phase
     */
    boolean hasErrors();

    /**
     * Return the knowledge builder results for the listed severities.
     *
     * @param severities
     */
    KnowledgeBuilderResults getResults(ResultSeverity... severities);

    boolean hasResults(ResultSeverity... problemTypes);

    default Collection<? extends KnowledgeBuilderResult> getAllResults() {
        return getResults(ResultSeverity.values());
    }

    default void add(KnowledgeBuilderResult result) {
        addBuilderResult(result);
    }

    default void addAll(Collection<? extends KnowledgeBuilderResult> results) {
        for (KnowledgeBuilderResult result : results) {
            add(result);
        }
    }
}
