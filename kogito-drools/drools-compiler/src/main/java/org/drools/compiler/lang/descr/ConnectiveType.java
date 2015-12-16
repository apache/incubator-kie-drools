/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.lang.descr;

/**
 * An enum for connective types
 */
public enum ConnectiveType {
    
    AND("&&", 2),
    OR("||", 1),
    XOR("^", 4),
    INC_OR("|", 3),
    INC_AND("&", 5);
    
    private String connective;
    // higher precedence connectives are executed before lower precedence
    private int    precedence;
    
    ConnectiveType( String connective, int precedence ) {
        this.connective = connective;
        this.precedence = precedence;
    }
    
    public String getConnective() {
        return this.connective;
    }
    
    public int getPrecedence() {
        return this.precedence;
    }
    
    public String toString() {
        return this.connective;
    }
}
