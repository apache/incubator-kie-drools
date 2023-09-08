/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.modelcompiler.constraints;

import java.util.ArrayList;
import java.util.List;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.Pattern;
import org.drools.base.time.Interval;
import org.drools.model.BitMask;
import org.drools.model.Index;
import org.drools.model.SingleConstraint;
import org.drools.model.functions.Predicate1;
import org.drools.model.functions.Predicate2;
import org.drools.model.functions.Predicate3;
import org.drools.model.functions.Predicate4;
import org.drools.model.functions.Predicate5;
import org.drools.model.functions.PredicateN;
import org.kie.api.runtime.rule.FactHandle;

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
        switch (declarations.length) {
            case 1:
                return declarations[0].isInternalFact() ?
                        new InnerEvaluator._1(patternDeclaration, declarations[0], constraint.getPredicate1()) :
                        new InnerEvaluator._1_FH(patternDeclaration, declarations[0], constraint.getPredicate1());
            case 2:
                return new InnerEvaluator._2(patternDeclaration, declarations[0], declarations[1], constraint.getPredicate2());
            case 3:
                return new InnerEvaluator._3(patternDeclaration, declarations[0], declarations[1], declarations[2],
                        constraint.getPredicate3());
            case 4:
                return new InnerEvaluator._4(patternDeclaration, declarations[0], declarations[1], declarations[2],
                        declarations[3], constraint.getPredicate4());
            case 5:
                return new InnerEvaluator._5(patternDeclaration, declarations[0], declarations[1], declarations[2],
                        declarations[3], declarations[4], constraint.getPredicate5());
            default:
                return new InnerEvaluator._N(patternDeclaration, declarations, constraint.getPredicate());
        }
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

    public boolean evaluate(FactHandle handle, ValueResolver reteEvaluator ) {
        try {
            return innerEvaluator.evaluate( handle, reteEvaluator );
        } catch (Exception e) {
            throw new RuntimeException( e );
        }
    }

    public boolean evaluate(FactHandle handle, BaseTuple tuple, ValueResolver reteEvaluator) {
        try {
            return innerEvaluator.evaluate( handle, tuple, reteEvaluator );
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
                switch (i) {
                    case 0:
                        if (innerEvaluator instanceof InnerEvaluator._1) {
                            (( InnerEvaluator._1 ) innerEvaluator).declaration = newDecl;
                        } else if (innerEvaluator instanceof InnerEvaluator._1_FH) {
                            (( InnerEvaluator._1_FH ) innerEvaluator).declaration = newDecl;
                        } else if (innerEvaluator instanceof InnerEvaluator._2) {
                            (( InnerEvaluator._2 ) innerEvaluator).declaration1 = newDecl;
                        } else if (innerEvaluator instanceof InnerEvaluator._3) {
                            (( InnerEvaluator._3 ) innerEvaluator).declaration1 = newDecl;
                        } else if (innerEvaluator instanceof InnerEvaluator._4) {
                            (( InnerEvaluator._4 ) innerEvaluator).declaration1 = newDecl;
                        } else if (innerEvaluator instanceof InnerEvaluator._5) {
                            (( InnerEvaluator._5 ) innerEvaluator).declaration1 = newDecl;
                        }
                        break;
                    case 1:
                        if (innerEvaluator instanceof InnerEvaluator._2) {
                            (( InnerEvaluator._2 ) innerEvaluator).declaration2 = newDecl;
                        } else if (innerEvaluator instanceof InnerEvaluator._3) {
                            (( InnerEvaluator._3 ) innerEvaluator).declaration2 = newDecl;
                        } else if (innerEvaluator instanceof InnerEvaluator._4) {
                            (( InnerEvaluator._4 ) innerEvaluator).declaration2 = newDecl;
                        } else if (innerEvaluator instanceof InnerEvaluator._5) {
                            (( InnerEvaluator._5 ) innerEvaluator).declaration2 = newDecl;
                        }
                        break;
                    case 2:
                        if (innerEvaluator instanceof InnerEvaluator._3) {
                            (( InnerEvaluator._3 ) innerEvaluator).declaration3 = newDecl;
                        } else if (innerEvaluator instanceof InnerEvaluator._4) {
                            (( InnerEvaluator._4 ) innerEvaluator).declaration3 = newDecl;
                        } else if (innerEvaluator instanceof InnerEvaluator._5) {
                            (( InnerEvaluator._5 ) innerEvaluator).declaration3 = newDecl;
                        }
                        break;
                    case 3:
                        if (innerEvaluator instanceof InnerEvaluator._4) {
                            (( InnerEvaluator._4 ) innerEvaluator).declaration4 = newDecl;
                        } else if (innerEvaluator instanceof InnerEvaluator._5) {
                            (( InnerEvaluator._5 ) innerEvaluator).declaration4 = newDecl;
                        }
                        break;
                    case 4:
                        if (innerEvaluator instanceof InnerEvaluator._5) {
                            (( InnerEvaluator._5 ) innerEvaluator).declaration5 = newDecl;
                        }
                        break;
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

        public abstract boolean evaluate(FactHandle handle, ValueResolver reteEvaluator ) throws Exception;
        public abstract boolean evaluate(FactHandle handle, BaseTuple tuple, ValueResolver reteEvaluator) throws Exception;

        protected Object getArgument(FactHandle handle, ValueResolver reteEvaluator, Declaration declaration, BaseTuple tuple ) {
            return declaration == patternDeclaration ? handle.getObject() : BindingEvaluator.getArgument( handle, reteEvaluator, declaration, tuple );
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
            public boolean evaluate(FactHandle handle, ValueResolver reteEvaluator ) throws Exception {
                return predicate.test( declaration.getValue( reteEvaluator, handle ) );
            }

            @Override
            public boolean evaluate(FactHandle handle, BaseTuple tuple, ValueResolver reteEvaluator ) throws Exception {
                return predicate.test( getArgument( handle, reteEvaluator, declaration, tuple ) );
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
            public boolean evaluate( FactHandle handle, ValueResolver reteEvaluator ) throws Exception {
                return predicate.test( handle.getObject() );
            }

            @Override
            public boolean evaluate( FactHandle handle, BaseTuple tuple, ValueResolver reteEvaluator ) throws Exception {
                return predicate.test( getArgument( handle, reteEvaluator, declaration, tuple ) );
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
            public boolean evaluate( FactHandle handle, ValueResolver reteEvaluator ) throws Exception {
                return predicate.test( getArgument( handle, reteEvaluator, declaration1, null ), getArgument( handle, reteEvaluator, declaration2, null ) );
            }

            @Override
            public boolean evaluate( FactHandle handle, BaseTuple tuple, ValueResolver reteEvaluator ) throws Exception {
                return predicate.test( getArgument( handle, reteEvaluator, declaration1, tuple ), getArgument( handle, reteEvaluator, declaration2, tuple ) );
            }
        }

        static class _3 extends InnerEvaluator {

            private Declaration declaration1;
            private Declaration declaration2;
            private Declaration declaration3;
            private final Predicate3 predicate;

            public _3( Declaration patternDeclaration, Declaration declaration1, Declaration declaration2,
                       Declaration declaration3, Predicate3 predicate ) {
                super( patternDeclaration );
                this.declaration1 = declaration1;
                this.declaration2 = declaration2;
                this.declaration3 = declaration3;
                this.predicate = predicate;
            }

            @Override
            public boolean evaluate(FactHandle handle, ValueResolver reteEvaluator ) throws Exception {
                return predicate.test( getArgument( handle, reteEvaluator, declaration1, null ),
                        getArgument( handle, reteEvaluator, declaration2, null ),
                        getArgument( handle, reteEvaluator, declaration3, null ));
            }

            @Override
            public boolean evaluate(FactHandle handle, BaseTuple tuple, ValueResolver reteEvaluator ) throws Exception {
                return predicate.test( getArgument( handle, reteEvaluator, declaration1, tuple ),
                        getArgument( handle, reteEvaluator, declaration2, tuple ),
                        getArgument( handle, reteEvaluator, declaration3, tuple ));
            }
        }

        static class _4 extends InnerEvaluator {

            private Declaration declaration1;
            private Declaration declaration2;
            private Declaration declaration3;
            private Declaration declaration4;
            private final Predicate4 predicate;

            public _4( Declaration patternDeclaration, Declaration declaration1, Declaration declaration2,
                       Declaration declaration3, Declaration declaration4, Predicate4 predicate ) {
                super( patternDeclaration );
                this.declaration1 = declaration1;
                this.declaration2 = declaration2;
                this.declaration3 = declaration3;
                this.declaration4 = declaration4;
                this.predicate = predicate;
            }

            @Override
            public boolean evaluate( FactHandle handle, ValueResolver reteEvaluator ) throws Exception {
                return predicate.test( getArgument( handle, reteEvaluator, declaration1, null ),
                        getArgument( handle, reteEvaluator, declaration2, null ),
                        getArgument( handle, reteEvaluator, declaration3, null ),
                        getArgument( handle, reteEvaluator, declaration4, null ));
            }

            @Override
            public boolean evaluate( FactHandle handle, BaseTuple tuple, ValueResolver reteEvaluator ) throws Exception {
                return predicate.test( getArgument( handle, reteEvaluator, declaration1, tuple ),
                        getArgument( handle, reteEvaluator, declaration2, tuple ),
                        getArgument( handle, reteEvaluator, declaration3, tuple ),
                        getArgument( handle, reteEvaluator, declaration4, tuple ));
            }
        }

        static class _5 extends InnerEvaluator {

            private Declaration declaration1;
            private Declaration declaration2;
            private Declaration declaration3;
            private Declaration declaration4;
            private Declaration declaration5;
            private final Predicate5 predicate;

            public _5( Declaration patternDeclaration, Declaration declaration1, Declaration declaration2,
                       Declaration declaration3, Declaration declaration4, Declaration declaration5,
                       Predicate5 predicate ) {
                super( patternDeclaration );
                this.declaration1 = declaration1;
                this.declaration2 = declaration2;
                this.declaration3 = declaration3;
                this.declaration4 = declaration4;
                this.declaration5 = declaration5;
                this.predicate = predicate;
            }

            @Override
            public boolean evaluate( FactHandle handle, ValueResolver reteEvaluator ) throws Exception {
                return predicate.test( getArgument( handle, reteEvaluator, declaration1, null ),
                        getArgument( handle, reteEvaluator, declaration2, null ),
                        getArgument( handle, reteEvaluator, declaration3, null ),
                        getArgument( handle, reteEvaluator, declaration4, null ),
                        getArgument( handle, reteEvaluator, declaration5, null ));
            }

            @Override
            public boolean evaluate( FactHandle handle, BaseTuple tuple, ValueResolver reteEvaluator ) throws Exception {
                return predicate.test( getArgument( handle, reteEvaluator, declaration1, tuple ),
                        getArgument( handle, reteEvaluator, declaration2, tuple ),
                        getArgument( handle, reteEvaluator, declaration3, tuple ),
                        getArgument( handle, reteEvaluator, declaration4, tuple ),
                        getArgument( handle, reteEvaluator, declaration5, tuple ));
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
            public boolean evaluate(FactHandle handle, ValueResolver reteEvaluator ) throws Exception {
                return predicate.test( getInvocationArgs( handle, null, reteEvaluator ) );
            }

            @Override
            public boolean evaluate(FactHandle handle, BaseTuple tuple, ValueResolver reteEvaluator ) throws Exception {
                return predicate.test( getInvocationArgs( handle, tuple, reteEvaluator ) );
            }

            private Object[] getInvocationArgs(FactHandle handle, BaseTuple tuple, ValueResolver reteEvaluator ) {
                Object[] params = new Object[declarations.length];
                for (int i = 0; i < declarations.length; i++) {
                    params[i] = getArgument( handle, reteEvaluator, declarations[i], tuple );
                }
                return params;
            }
        }
    }
}
