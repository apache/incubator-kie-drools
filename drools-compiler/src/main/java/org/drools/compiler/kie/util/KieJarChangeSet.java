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
package org.drools.compiler.kie.util;

import java.util.HashMap;
import java.util.Map;

import org.kie.internal.builder.ChangeType;
import org.kie.internal.builder.ResourceChangeSet;

public class KieJarChangeSet {
    private final Map<String, ResourceChangeSet> changes = new HashMap<>();

    public Map<String, ResourceChangeSet> getChanges() {
        return changes;
    }

    public void removeFile(String file) {
        changes.put( file, new ResourceChangeSet( file, ChangeType.REMOVED ) );
    }

    public void addFile(String file) {
        changes.put( file, new ResourceChangeSet( file, ChangeType.ADDED ) );
    }

    public void registerChanges(String file, ResourceChangeSet changeSet) {
        changes.put( file, changeSet );
    }

    public boolean contains(String resourceName) {
        return changes.containsKey(resourceName);
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
        result = prime * result + changes.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        KieJarChangeSet other = (KieJarChangeSet) obj;
        return changes.equals(other.changes);
    }

    @Override
    public String toString() {
        return "KieJarChangeSet [changes=" + changes + "]";
    }
}
