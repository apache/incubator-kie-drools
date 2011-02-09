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

package org.drools.lang.descr;

public class BindingDescr extends BaseDescr {
    
    private static final long serialVersionUID = 520l;
    
    private String               variable;
    private String               expression;

    public BindingDescr() {
        this( null,
              null );
    }

    public BindingDescr(final String variable,
                     final String expression) {
        this.variable = variable;
        this.expression = expression;
    }

    public void setVariable(final String variable) {
        this.variable = variable;
    }

    public void setExpression(final String expression) {
        this.expression = expression;
    }

    public String getVariable() {
        return this.variable;
    }

    public String getExpression() {
        return this.expression;
    }

    public String toString() {
        return "[Binding: " + this.variable + " : " + this.expression + "]";
    }
}
