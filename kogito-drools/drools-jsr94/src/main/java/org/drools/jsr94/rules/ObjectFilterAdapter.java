/**
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

package org.drools.jsr94.rules;

import javax.rules.ObjectFilter;

/**
 * Adaptor class, that makes JSR94 ObjectFilters work from a delegating Drools ObjectFilter
 * @author mproctor
 *
 */
public class ObjectFilterAdapter implements org.drools.runtime.ObjectFilter {
    private ObjectFilter filter;
    
    public ObjectFilterAdapter(ObjectFilter filter) {
        this.filter = filter;
    }

    public boolean accept(Object object) {
        return ( this.filter == null || this.filter.filter( object ) != null );
    }
}
