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
package org.kie.internal.builder;



public class ResourceChange {
    public static enum Type {
        RULE, DECLARATION, FUNCTION, GLOBAL, PROCESS;
        public String toString() {
            return super.toString().toLowerCase();
        }
    }
    private final ChangeType action;
    private final ResourceChange.Type type;
    private final String name;
    public ResourceChange(ChangeType action,
                          ResourceChange.Type type,
                          String name) {
        super();
        this.action = action;
        this.type = type;
        this.name = name;
    }
    public ChangeType getChangeType() {
        return action;
    }
    public ResourceChange.Type getType() {
        return type;
    }
    public String getName() {
        return name;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((action == null) ? 0 : action.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) { return true; }
        if ( obj == null ) { return false; }
        if ( getClass() != obj.getClass() ) { return false; }
        ResourceChange other = (ResourceChange) obj;
        if ( action != other.action ) { return false; }
        if ( name == null ) {
            if ( other.name != null ) { return false; }
        } else if ( !name.equals( other.name ) ) {
            return false;
        }
        if ( type != other.type ) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "ResourceChange [action=" + action + ", type=" + type + ", name=" + name + "]";
    }

}
