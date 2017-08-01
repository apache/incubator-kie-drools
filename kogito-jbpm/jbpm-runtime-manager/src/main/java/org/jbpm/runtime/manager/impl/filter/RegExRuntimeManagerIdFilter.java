/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.runtime.manager.impl.filter;

import java.util.Collection;
import java.util.HashSet;

import org.kie.internal.runtime.manager.RuntimeManagerIdFilter;

/**
 * Regular expression based filtering for runtime manager identifiers
 *
 */
public class RegExRuntimeManagerIdFilter implements RuntimeManagerIdFilter {

    @Override
    public Collection<String> filter(String pattern, Collection<String> identifiers) {
        Collection<String> outputCollection = new HashSet<String>();
        for (String identifier : identifiers) {
            if (identifier.matches(pattern)) {
                outputCollection.add(identifier);
            }
        }
        
        return outputCollection;
    }

}
