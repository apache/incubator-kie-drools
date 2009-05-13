/*
 * Copyright 2008 Red Hat
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
 *
 */
package org.drools;

public class Worker {
    
    private String id;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    
    @Override
    public int hashCode() {
        return 3;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof Worker)) return false;
        Worker other = (Worker) obj;
        return nullSafeEquals(this.id, other.id);
    }
    
    private boolean nullSafeEquals(Object obj1, Object obj2) {
        if (obj1 == obj2) return true;
        if (obj1 == null && obj2 != null) return false;
        return obj1.equals(obj2);
    }
}