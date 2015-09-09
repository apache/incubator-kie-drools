/*
 * Copyright 2015 JBoss by Red Hat.
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

package org.kie.internal.runtime.manager;

import java.util.Collection;

/**
 * Allows to apply filtering on runtime manager identifiers to find only those matching
 *
 */
public interface RuntimeManagerIdFilter {

    /**
     * Filters given <code>identifiers</code> based on given pattern and return only those matching.
     * @param pattern pattern used to filter identifiers
     * @param identifiers all available identifiers
     * @return returns only matched identifiers or empty list in case of no match found
     */
    Collection<String> filter(String pattern, Collection<String> identifiers);
}
