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

/**
 * This represents a constraint in a pattern defined by an arbitrary
 * expression. The constraint can be any valid java/mvel expression.
 */
public class ExprConstraintDescr extends BaseDescr {

    private static final long serialVersionUID = 520l;

    public ExprConstraintDescr() {
        super();
    }
    
    public ExprConstraintDescr(final String expr) {
        super();
        setText( expr );
    }
    
    public void setExpression( final String expr ) {
        setText( expr );
    }
    
    public String getExpression( ) {
        return getText();
    }

    @Override
    public String toString() {
        return getText();
    }
}
