/*
 * Copyright 2010 JBoss Inc
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

package org.drools.impl;

import java.util.Collection;
import java.util.Map;

import org.drools.runtime.StatelessKnowledgeSessionResults;

public class StatelessKnowledgeSessionResultsImpl implements StatelessKnowledgeSessionResults {

    private Map<String, ?> results;
    
    public StatelessKnowledgeSessionResultsImpl(Map<String, ?> results) {
        this.results = results;
    }
    
    public Collection<String> getIdentifiers() {
        return results.keySet();
    }

    public Object getValue(String identifier) {
        return results.get( identifier );
    }

}
