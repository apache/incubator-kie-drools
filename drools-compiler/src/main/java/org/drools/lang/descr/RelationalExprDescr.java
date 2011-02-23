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
 * A descriptor to represent a relational expression
 */
public class RelationalExprDescr extends BaseDescr {
    private static final long serialVersionUID = 520l;

    private BaseDescr         left;
    private BaseDescr         right;
    private String            operator;

    public RelationalExprDescr() {
    }

    public RelationalExprDescr(String operator,
                               BaseDescr left,
                               BaseDescr right) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public BaseDescr getLeft() {
        return left;
    }

    public void setLeft( BaseDescr left ) {
        this.left = left;
    }

    public BaseDescr getRight() {
        return right;
    }

    public void setRight( BaseDescr right ) {
        this.right = right;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator( String operator ) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return this.left + " " + this.operator + " " + this.right;
    }
}
