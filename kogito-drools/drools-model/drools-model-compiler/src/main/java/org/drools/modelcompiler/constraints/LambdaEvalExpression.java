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

package org.drools.modelcompiler.constraints;

import org.drools.core.WorkingMemory;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;
import org.drools.core.spi.EvalExpression;
import org.drools.core.spi.Tuple;
import org.drools.model.SingleConstraint;

public class LambdaEvalExpression implements EvalExpression {

    private final ConstraintEvaluator evaluator;

    public LambdaEvalExpression(ConstraintEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    public LambdaEvalExpression(Pattern pattern, SingleConstraint constraint) {
        this( new ConstraintEvaluator(pattern, constraint) );
    }

    @Override
    public Object createContext() {
        return null;
    }

    @Override
    public boolean evaluate(Tuple tuple, Declaration[] requiredDeclarations, WorkingMemory workingMemory, Object context) throws Exception {
        return evaluator.evaluate(tuple.getFactHandle(), tuple);
    }

    @Override
    public void replaceDeclaration(Declaration declaration, Declaration resolved) {
        evaluator.replaceDeclaration(declaration, resolved);
    }

    @Override
    public EvalExpression clone() {
        return new LambdaEvalExpression( evaluator.clone() );
    }

    public static final EvalExpression EMPTY = new EvalExpression() {
        @Override
        public Object createContext() {
            return null;
        }

        @Override
        public boolean evaluate(Tuple tuple, Declaration[] requiredDeclarations, WorkingMemory workingMemory, Object context) throws Exception {
            return true;
        }

        @Override
        public void replaceDeclaration(Declaration declaration, Declaration resolved) { }

        @Override
        public EvalExpression clone() {
            return this;
        }
    };
}
