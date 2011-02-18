/*
 * Copyright 2011 JBoss Inc
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

package org.drools.lang.descr;

/**
 * An enum for connective types
 */
public enum ConnectiveType {
    
    AND("&&"),
    OR("||"),
    XOR("^"),
    INC_OR("|"),
    INC_AND("&");
    
    private String connective;
    
    ConnectiveType( String connective ) {
        this.connective = connective;
    }
    
    public String getConnective() {
        return this.connective;
    }
    
    public String toString() {
        return this.connective;
    }
}
