package org.drools.base;

/*
 * Copyright 2005 JBoss Inc
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

public class DroolsQuery {
    private final String name;
    private final Object[] args;
    
    private static final Object[] EMPTY_PARAMS = new Object[0];

    public DroolsQuery(final String name) {
        super();
        this.name = name;
        this.args = EMPTY_PARAMS;
    }
    
    public DroolsQuery(final String name, final Object[] params) {
        super();
        this.name = name;
        if ( params != null ) {
            this.args = params;
        } else {
            this.args = EMPTY_PARAMS;
        }
    }    

    public String getName() {
        return this.name;
    }
    
    public Object[] getArguments() {
        return this.args;
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((this.name == null) ? 0 : this.name.hashCode());
        return result;
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || getClass() != object.getClass() ) {
            return false;
        }

        final DroolsQuery other = (DroolsQuery) object;
        return this.name.equals( other.name );
    }
}