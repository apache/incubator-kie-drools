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

package org.drools.compiler.kie.util;

import org.kie.internal.builder.ResourceChangeSet;

import java.util.HashMap;
import java.util.Map;

public class KieJarChangeSet {
    private final Map<String, ResourceChangeSet> changes = new HashMap<String, ResourceChangeSet>();

    public Map<String, ResourceChangeSet> getChanges() {
        return changes;
    }

    public boolean contains(String resourceName) {
        return changes.keySet().contains(resourceName);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((changes == null) ? 0 : changes.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        KieJarChangeSet other = (KieJarChangeSet) obj;
        if ( changes == null ) {
            if ( other.changes != null ) return false;
        } else if ( !changes.equals( other.changes ) ) return false;
        return true;
    }

    @Override
    public String toString() {
        return "KieJarChangeSet [changes=" + changes + "]";
    }
}
