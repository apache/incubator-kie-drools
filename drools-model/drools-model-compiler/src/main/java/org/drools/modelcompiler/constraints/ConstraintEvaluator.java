/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.constraints;

import java.util.ArrayList;
import java.util.List;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;
import org.drools.core.spi.Tuple;
import org.drools.core.time.Interval;
import org.drools.model.BitMask;
import org.drools.model.Index;
import org.drools.model.SingleConstraint;
import org.drools.model.functions.Predicate1;
import org.drools.model.functions.Predicate2;
import org.drools.model.functions.PredicateN;

public class ConstraintEvaluator {

    protected final SingleConstraint constraint;

    protected final Declaration[] declarations;
    private Declaration[] requiredDeclarations;

    private final Pattern pattern;

    private final InnerEvaluator innerEvaluator;

    public ConstraintEvaluator(Declaration[] declarations, SingleConstraint constraint) {
        this.constraint = constraint;
        this.pattern = null;
        this.declarations = declarations;
        this.requiredDeclarations = declarations;
        this.innerEvaluator = initInnerEvaluator(null);
    }

    public ConstraintEvaluator(Pattern pattern, SingleConstraint constraint) {
        this.constraint = constraint;
        this.pattern = pattern;
        this.declarations = new Declaration[] { pattern.getDeclaration() };
        this.requiredDeclarations = new Declaration[0];
        this.innerEvaluator = initInnerEvaluator(findPatternDeclaration());
    }

    public ConstraintEvaluator(Declaration[] declarations, Pattern pattern, SingleConstraint constraint) {
        this.constraint = constraint;
        this.declarations = declarations;
        this.pattern = pattern;
        this.innerEvaluator = initInnerEvaluator(findPatternAndRequiredDeclaration());
    }

    public SingleConstraint getConstraint() {
        return constraint;
    }

    private InnerEvaluator initInnerEvaluator(Declaration patternDeclaration) {
        if (isTemporal()) {
            setPatternDeclaration( patternDeclaration );
            return null;
        }
        if (declarations.length == 1) {
            return declarations[0].isInternalFact() ?
                    new InnerEvaluator._1(patternDeclaration, declarations[0], constraint.getPredicate1()) :
                    new InnerEvaluator._1_FH(patternDeclaration, declarations[0], constraint.getPredicate1());
        }
        if (declarations.length == 2) {
            return new InnerEvaluator._2(patternDeclaration, declarations[0], declarations[1], constraint.getPredicate2());
        }
        return new InnerEvaluator._N(patternDeclaration, declarations, constraint.getPredicate());
    }

    private Declaration findPatternDeclaration() {
        for ( int i = 0; i < declarations.length; i++ ) {
            if ( pattern.getDeclaration().getIdentifier().equals( declarations[i].getIdentifier() ) ) {
                return declarations[i];
            }
        }
        return null;
    }

    private Declaration findPatternAndRequiredDeclaration() {
        Declaration patternDeclaration = null;
        List<Declaration> requiredDeclarationsList = new ArrayList<>();
        for ( int i = 0; i < declarations.length; i++ ) {
            if ( pattern.getDeclaration() != null && pattern.getDeclaration().getIdentifier().equals( declarations[i].getIdentifier() ) ) {
                patternDeclaration = declarations[i];
            } else {
                requiredDeclarationsList.add(declarations[i]);
            }
        }
        this.requiredDeclarations = requiredDeclarationsList.toArray( new Declaration[requiredDeclarationsList.size()] );
        return patternDeclaration;
    }

    public boolean evaluate( InternalFactHandle handle, InternalWorkingMemory workingMemory ) {
        try {
            return innerEvaluator.evaluate( handle, workingMemory );
        } catch (Exception e) {
            throw new RuntimeException( e );
        }
    }

    public boolean evaluate(InternalFactHandle handle, Tuple tuple, InternalWorkingMemory workingMemory) {
        try {
            return innerEvaluator.evaluate( handle, tuple, workingMemory );
        } catch (Exception e) {
            throw new RuntimeException( e );
        }
    }

    public Index getIndex() {
        return constraint.getIndex();
    }

    public String[] getReactiveProps() {
        return constraint.getReactiveProps();
    }

    public BitMask getReactivityBitMask() {
        return constraint.getReactivityBitMask();
    }

    @Override
    public String toString() {
        return constraint.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        ConstraintEvaluator otherEval = (ConstraintEvaluator) other;
        if (!getId().equals(otherEval.getId())) return false;
        if (declarations.length != otherEval.declarations.length) return false;
        for (int i = 0; i < declarations.length; i++) {
            if (!declarations[i].getExtractor().equals( otherEval.declarations[i].getExtractor() )) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    public String getId() {
        return constraint.getExprId();
    }

    public Declaration[] getRequiredDeclarations() {
        return requiredDeclarations;
    }

    public void replaceDeclaration(Declaration oldDecl, Declaration newDecl) {
        for ( int i = 0; i < declarations.length; i++) {
            if ( declarations[i].equals( oldDecl )) {
                declarations[i] = newDecl;
                if (i == 0) {
                    if (innerEvaluator instanceof InnerEvaluator._1) {
                        (( InnerEvaluator._1 ) innerEvaluator).declaration = newDecl;
                    } else if (innerEvaluator instanceof InnerEvaluator._1_FH) {
                        (( InnerEvaluator._1_FH ) innerEvaluator).declaration = newDecl;
                    } else if (innerEvaluator instanceof InnerEvaluator._2) {
                        (( InnerEvaluator._2 ) innerEvaluator).declaration1 = newDecl;
                    }
                }
                if (i == 1 && innerEvaluator instanceof InnerEvaluator._2) {
                    (( InnerEvaluator._2 ) innerEvaluator).declaration2 = newDecl;
                }
                break;
            }
        }
    }

    public ConstraintEvaluator clone() {
        return pattern == null ?
                new ConstraintEvaluator( getClonedDeclarations(), constraint ) :
                new ConstraintEvaluator( getClonedDeclarations(), pattern, constraint );
    }

    protected Declaration[] getClonedDeclarations() {
        Declaration[] clonedDeclarations = new Declaration[declarations.length];
        for (int i = 0; i < declarations.length; i++) {
            clonedDeclarations[i] = declarations[i].clone();
        }
        return clonedDeclarations;
    }

    protected Declaration[] getDeclarations() {
        return declarations;
    }

    protected Pattern getPattern() {
        return pattern;
    }

    public boolean isTemporal() {
        return false;
    }

    public Interval getInterval() {
        throw new UnsupportedOperationException();
    }

    protected void setPatternDeclaration( Declaration patternDeclaration ) {
        throw new UnsupportedOperationException();
    }

    static abstract class InnerEvaluator {

        private final Declaration patternDeclaration;

        protected InnerEvaluator( Declaration patternDeclaration ) {
            this.patternDeclaration = patternDeclaration;
        }

        public abstract boolean evaluate( InternalFactHandle handle, InternalWorkingMemory workingMemory ) throws Exception;
        public abstract boolean evaluate(InternalFactHandle handle, Tuple tuple, InternalWorkingMemory workingMemory) throws Exception;

        protected Object getArgument( InternalFactHandle handle, InternalWorkingMemory workingMemory, Declaration declaration, Tuple tuple ) {
            return declaration == patternDeclaration ? handle.getObject() : BindingEvaluator.getArgument( handle, workingMemory, declaration, tuple );
        }

        static class _1 extends InnerEvaluator {

            private Declaration declaration;
            private final Predicate1 predicate;

            public _1( Declaration patternDeclaration, Declaration declaration, Predicate1 predicate ) {
                super( patternDeclaration );
                this.declaration = declaration;
                this.predicate = predicate;
            }

            @Override
            public boolean evaluate( InternalFactHandle handle, InternalWorkingMemory workingMemory ) throws Exception {
                return predicate.test( declaration.getValue( workingMemory, handle ) );
            }

            @Override
            public boolean evaluate( InternalFactHandle handle, Tuple tuple, InternalWorkingMemory workingMemory ) throws Exception {
                return predicate.test( getArgument( handle, workingMemory, declaration, tuple ) );
            }
        }

        static class _1_FH extends InnerEvaluator {

            private Declaration declaration;
            private final Predicate1 predicate;

            public _1_FH( Declaration patternDeclaration, Declaration declaration, Predicate1 predicate ) {
                super( patternDeclaration );
                this.declaration = declaration;
                this.predicate = predicate;
            }

            @Override
            public boolean evaluate( InternalFactHandle handle, InternalWorkingMemory workingMemory ) throws Exception {
                return predicate.test( handle.getObject() );
            }

            @Override
            public boolean evaluate( InternalFactHandle handle, Tuple tuple, InternalWorkingMemory workingMemory ) throws Exception {
                return predicate.test( getArgument( handle, workingMemory, declaration, tuple ) );
            }
        }

        static class _2 extends InnerEvaluator {

            private Declaration declaration1;
            private Declaration declaration2;
            private final Predicate2 predicate;

            public _2( Declaration patternDeclaration, Declaration declaration1, Declaration declaration2, Predicate2 predicate ) {
                super( patternDeclaration );
                this.declaration1 = declaration1;
                this.declaration2 = declaration2;
                this.predicate = predicate;
            }

            @Override
            public boolean evaluate( InternalFactHandle handle, InternalWorkingMemory workingMemory ) throws Exception {
                return predicate.test( getArgument( handle, workingMemory, declaration1, null ), getArgument( handle, workingMemory, declaration2, null ) );
            }

            @Override
            public boolean evaluate( InternalFactHandle handle, Tuple tuple, InternalWorkingMemory workingMemory ) throws Exception {
                return predicate.test( getArgument( handle, workingMemory, declaration1, tuple ), getArgument( handle, workingMemory, declaration2, tuple ) );
            }
        }

        static class _N extends InnerEvaluator {

            private final Declaration[] declarations;
            private final PredicateN predicate;

            public _N( Declaration patternDeclaration, Declaration[] declarations, PredicateN predicate ) {
                super( patternDeclaration );
                this.declarations = declarations;
                this.predicate = predicate;
            }

            @Override
            public boolean evaluate( InternalFactHandle handle, InternalWorkingMemory workingMemory ) throws Exception {
                return predicate.test( getInvocationArgs( handle, null, workingMemory ) );
            }

            @Override
            public boolean evaluate( InternalFactHandle handle, Tuple tuple, InternalWorkingMemory workingMemory ) throws Exception {
                return predicate.test( getInvocationArgs( handle, tuple, workingMemory ) );
            }

            private Object[] getInvocationArgs( InternalFactHandle handle, Tuple tuple, InternalWorkingMemory workingMemory ) {
                Object[] params = new Object[declarations.length];
                for (int i = 0; i < declarations.length; i++) {
                    params[i] = getArgument( handle, workingMemory, declarations[i], tuple );
                }
                return params;
            }
        }
    }
}
