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
 * expression. The constraint can be a single expression without comparative
 * operators that returns a boolean value, for instance "isEmpty()", or it 
 * can be a complete expression including an operator, and which also returns
 * a boolean value as a result, e.g. "x > 20". Any valid java/mvel expression
 * is supported.
 */
public class ExprConstraintDescr extends BaseDescr {

    private static final long serialVersionUID = 510l;
    private String            leftSide;
    private String            operator;
    private String            rightSide;

    public ExprConstraintDescr() {
        super();
    }
    
    public ExprConstraintDescr(final String leftSide) {
        this.leftSide = leftSide;
    }

    public String getLeftSide() {
        return leftSide;
    }

    public void setLeftSide( String leftSide ) {
        this.leftSide = leftSide;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator( String operator ) {
        this.operator = operator;
    }

    public String getRightSide() {
        return rightSide;
    }

    public void setRightSide( String rightSide ) {
        this.rightSide = rightSide;
    }
    
    public boolean isSingleValue() {
        return this.operator == null;
    }
    
    public boolean isCompleteExpr() {
        return this.operator != null;
    }

    @Override
    public String toString() {
        return leftSide + ((operator!=null) ? " " + operator + " " + rightSide : "");
    }
}
