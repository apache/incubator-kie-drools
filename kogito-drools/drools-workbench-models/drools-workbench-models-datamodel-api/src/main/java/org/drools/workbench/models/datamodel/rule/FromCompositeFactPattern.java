/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.models.datamodel.rule;

public class FromCompositeFactPattern implements IFactPattern {

    private FactPattern factPattern;
    private ExpressionFormLine expression = new ExpressionFormLine();

    public FromCompositeFactPattern() {
    }

    public ExpressionFormLine getExpression() {
        return expression;
    }

    public void setExpression( ExpressionFormLine expression ) {
        this.expression = expression;
    }

    public FactPattern getFactPattern() {
        return factPattern;
    }

    public void setFactPattern( FactPattern pattern ) {
        this.factPattern = pattern;
    }

    public String getFactType() {
        if ( this.factPattern == null ) {
            return null;
        }

        return this.factPattern.getFactType();
    }

}
