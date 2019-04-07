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

package org.kie.internal.builder;

import java.util.ArrayList;
import java.util.List;

public class ResourceChangeSet {
    private final String     resourceName;  // src/main/resources/org/drools/rules.drl
    private final ChangeType status;
    private final List<ResourceChange> changes = new ArrayList<ResourceChange>();
    private List<RuleLoadOrder> loadOrder = new ArrayList<RuleLoadOrder>();

    public ResourceChangeSet(String resourceName, ChangeType status) {
        this.resourceName = resourceName;
        this.status = status;
    }

    public String getResourceName() {
        return resourceName;
    }

    public ChangeType getChangeType() {
        return status;
    }

    public List<ResourceChange> getChanges() {
        return changes;
    }

    public List<RuleLoadOrder> getLoadOrder() {
        return loadOrder;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((changes == null) ? 0 : changes.hashCode());
        result = prime * result + ((resourceName == null) ? 0 : resourceName.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) { return true; }
        if ( obj == null ) { return false; }
        if ( getClass() != obj.getClass() ) { return false; }
        ResourceChangeSet other = (ResourceChangeSet) obj;
        if ( changes == null ) {
            if ( other.changes != null ) { return false; }
        } else if ( !changes.equals( other.changes ) ) {
            return false;
        }
        if ( resourceName == null ) {
            if ( other.resourceName != null ) { return false; }
        } else if ( !resourceName.equals( other.resourceName ) ) {
            return false;
        }
        if ( status != other.status ) {
            return false;
        }

        return true;
    }

    public static class RuleLoadOrder {
        private String pkgName;
        private String ruleName;
        private int loadOrder;

        public RuleLoadOrder(String pkgName, String ruleName, int loadOrder) {
            this.pkgName = pkgName;
            this.ruleName = ruleName;
            this.loadOrder = loadOrder;
        }

        public String getPkgName() {
            return pkgName;
        }

        public String getRuleName() {
            return ruleName;
        }

        public int getLoadOrder() {
            return loadOrder;
        }

    }
}
