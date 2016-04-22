/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashSet;
import java.util.Set;

public class Person {
    private String name;

    private Set    sisters;

    public Person() {
        
    }
    
    public Person(final String name) {
        this.name = name;
        this.sisters = new HashSet();
    }

    public String getName() {
        return this.name;
    }

    public void addSister(final String sistersName) {
        this.sisters.add( sistersName );
    }

    public boolean hasSister(final Person person) {
        return this.sisters.contains( person.getName() );
    }

    public String toString() {
        return this.name;
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((name == null) ? 0 : name.hashCode());
        result = PRIME * result + ((sisters == null) ? 0 : sisters.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        
        if ( !(obj instanceof Person) ) return false;
        final Person other = (Person) obj;
        
        if ( name == null ) {
            if ( other.name != null ) return false;
        } else if ( !name.equals( other.name ) ) return false;
        if ( sisters == null ) {
            if ( other.sisters != null ) return false;
        } else if ( !sisters.equals( other.sisters ) ) return false;
        return true;
    }
    
    
}
