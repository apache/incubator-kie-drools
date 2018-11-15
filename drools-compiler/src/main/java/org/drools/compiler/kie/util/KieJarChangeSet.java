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

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.kie.internal.builder.ChangeType;
import org.kie.internal.builder.ResourceChangeSet;

public class KieJarChangeSet {
    static class ChangeSet {
        final ResourceChangeSet resourceChangeSet;
        final Boolean isExecutableModel;

        ChangeSet(ResourceChangeSet resourceChangeSet, Boolean isExecutableModel) {
            this.resourceChangeSet = resourceChangeSet;
            this.isExecutableModel = isExecutableModel;
        }

        ResourceChangeSet getResourceChangeSet() {
            return resourceChangeSet;
        }

        @Override
        public String toString() {
            return "ChangeSet{" +
                    "resourceChangeSet=" + resourceChangeSet +
                    ", isExecutableModel=" + isExecutableModel +
                    '}';
        }
    }


    private final Map<String, ChangeSet> changes = new HashMap<>();


    public Map<String, ResourceChangeSet> getChanges() {
        return changes.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, p -> p.getValue().getResourceChangeSet()));
    }

    public void removeFile(String file) {
        changes.put( file, new ChangeSet(new ResourceChangeSet( file, ChangeType.REMOVED ), false)  );
    }

    public void addFile(String file) {
        changes.put( file, new ChangeSet(new ResourceChangeSet( file, ChangeType.ADDED ), false) );
    }

    public void registerChanges(String file, ResourceChangeSet changeSet) {
        changes.put( file, new ChangeSet(changeSet, false) );
    }

    public void registerChanges(String file, ResourceChangeSet changeSet, Boolean isExecutableModel) {
        changes.put( file, new ChangeSet(changeSet, isExecutableModel) );
    }

    public boolean contains(String resourceName) {
        return changes.keySet().contains(resourceName);
    }

    public KieJarChangeSet merge(KieJarChangeSet other) {
        KieJarChangeSet merged = new KieJarChangeSet();
        merged.changes.putAll(this.changes);
        merged.changes.putAll(other.changes);
        return merged;
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
