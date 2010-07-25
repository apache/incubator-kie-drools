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

package org.drools.runtime;

/**
 * Filters Objects by Class, only accepting Classes of the specified type
 *
 */
public class ClassObjectFilter
    implements
    ObjectFilter {
    private Class clazz;

    /** 
     * The Allowed Class type
     * @param clazz
     */
    public ClassObjectFilter(Class clazz) {
        this.clazz = clazz;
    }

    /**
     * Returning true means the Iterator accepts, and thus returns, the current Object's Class type.
     * @param object
     * @return
     */
    public boolean accept(Object object) {
        return this.clazz.isAssignableFrom( object.getClass() );
    }

}
