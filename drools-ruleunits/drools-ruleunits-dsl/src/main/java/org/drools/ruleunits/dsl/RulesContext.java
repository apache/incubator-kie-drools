/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.drools.ruleunits.dsl;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.drools.core.util.PropertyReactivityUtil;
import org.drools.model.AlphaIndex;
import org.drools.model.BetaIndex;
import org.drools.model.DSL;
import org.drools.model.Global;
import org.drools.model.Index;
import org.drools.model.Model;
import org.drools.model.PatternDSL;
import org.drools.model.Rule;
import org.drools.model.RuleItemBuilder;
import org.drools.model.Variable;
import org.drools.model.functions.Block1;
import org.drools.model.functions.Block2;
import org.drools.model.functions.Block3;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Predicate1;
import org.drools.model.impl.ModelImpl;
import org.drools.model.impl.RuleBuilder;
import org.drools.ruleunits.api.DataSource;

import static org.drools.model.DSL.declarationOf;
import static org.drools.model.DSL.entryPoint;
import static org.drools.model.DSL.globalOf;
import static org.drools.model.PatternDSL.alphaIndexedBy;
import static org.drools.model.PatternDSL.betaIndexedBy;
import static org.drools.model.PatternDSL.pattern;
import static org.drools.model.PatternDSL.rule;

public class RulesContext {

    private final RuleUnitDefinition unit;

    private final List<UnitRule> rules = new ArrayList<>();
    private final Map<Object, Global> globals = new IdentityHashMap<>();

    public RulesContext(RuleUnitDefinition unit) {
        this.unit = unit;
    }

    public UnitRule addRule() {
        return addRule(UUID.randomUUID().toString());
    }

    public UnitRule addRule(String name) {
        UnitRule rule = new UnitRule(this, name);
        rules.add(rule);
        return rule;
    }

    Model toModel() {
        ModelImpl model = new ModelImpl();
        globals.values().forEach(model::addGlobal);
        rules.stream().map(RulesContext.UnitRule::toRule).forEach(model::addRule);
        return model;
    }

    Map<Object, Global> getGlobals() {
        return globals;
    }

    public static class UnitRule {

        private final RulesContext context;
        private final String name;

        private final List<PatternDefinition> patterns = new ArrayList<>();
        private RuleItemBuilder consequence;

        public UnitRule(RulesContext context, String name) {
            this.context = context;
            this.name = name;
        }

        public <A> Pattern1<A> from(DataSource<A> dataSource) {
            Pattern1<A> pattern1 = new Pattern1<>(this, declarationOf(findDataSourceClass(dataSource), entryPoint(asGlobal(dataSource).getName())));
            patterns.add(pattern1);
            return pattern1;
        }

        <A, B> Pattern2<A, B> join(Pattern1<A> pattern1, DataSource<B> dataSource) {
            Pattern2<A, B> pattern2 = new Pattern2<>(pattern1, this, declarationOf(findDataSourceClass(dataSource), entryPoint(asGlobal(dataSource).getName())));
            patterns.add(pattern2);
            return pattern2;
        }

        private <A> Class<A> findDataSourceClass(DataSource<A> dataSource) {
            assert(dataSource != null);
            for (Field field : context.unit.getClass().getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    if (dataSource == field.get(context.unit)) {
                        Type dsType = field.getGenericType();
                        if (dsType instanceof ParameterizedType) {
                            return (Class<A>) ((ParameterizedType) dsType).getActualTypeArguments()[0];
                        }
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            throw new IllegalArgumentException("Unknown DataSource type");
        }

        public <T> void execute(T globalObject, Block1<T> block) {
            consequence = DSL.on(asGlobal(globalObject)).execute(block);
        }

        private <T> Global asGlobal(T globalObject) {
            return context.globals.computeIfAbsent(globalObject, o -> globalOf(o.getClass(), context.unit.getClass().getCanonicalName(), UUID.randomUUID().toString()));
        }

        public Rule toRule() {
            RuleBuilder ruleBuilder = rule(context.unit.getClass().getCanonicalName(), name).unit(context.unit.getClass());

            List<RuleItemBuilder> items = new ArrayList<>();

            for (PatternDefinition<?, ?> pattern : patterns) {
                PatternDSL.PatternDef patternDef = pattern(pattern.getVariable());
                for (Constraint constraint : pattern.getConstraints()) {
                    constraint.addConstraintToPattern(patternDef);
                }
                items.add(patternDef);
            }

            if (consequence != null) {
                items.add(consequence);
            }

            return ruleBuilder.build(items.toArray(new RuleItemBuilder[items.size()]));
        }
    }

    public static abstract class PatternDefinition<A, P extends PatternDefinition> {
        protected final UnitRule rule;
        protected final Variable<A> variable;
        protected final List<Constraint> constraints = new ArrayList<>();

        protected PatternDefinition(UnitRule rule, Variable<A> variable) {
            this.rule = rule;
            this.variable = variable;
        }

        protected List<Constraint> getConstraints() {
            return constraints;
        }

        protected Variable getVariable() {
            return variable;
        }

        public P filter(Predicate1<A> predicate) {
            constraints.add(patternDef -> patternDef.expr(UUID.randomUUID().toString(), predicate));
            return (P) this;
        }

        public P filter(String fieldName, Predicate1<A> predicate) {
            constraints.add(patternDef -> patternDef.expr(UUID.randomUUID().toString(), predicate, PatternDSL.reactOn(fieldName)));
            return (P) this;
        }

        public P filter(Index.ConstraintType constraintType, A rightValue) {
            return filter("this", a -> a, constraintType, rightValue);
        }

        public <V> P filter(Function1<A, V> extractor, Index.ConstraintType constraintType, V rightValue) {
            return filter(null, extractor, constraintType, rightValue);
        }

        public <V> P filter(String fieldName, Function1<A, V> extractor, Index.ConstraintType constraintType, V rightValue) {
            constraints.add(new AlphaConstraint<>(variable, fieldName, extractor, constraintType, rightValue));
            return (P) this;
        }

        public <G> void execute(G globalObject, Block1<G> block) {
            rule.execute(globalObject, block);
        }
    }

    public static class Pattern1<A> extends PatternDefinition<A, Pattern1<A>> {

        public Pattern1(UnitRule rule, Variable<A> variable) {
            super(rule, variable);
        }

        public void execute(Block1<A> block) {
            rule.consequence = DSL.on(variable).execute(block);
        }

        public <G> void execute(G globalObject, Block2<G, A> block) {
            rule.consequence = DSL.on(rule.asGlobal(globalObject), variable).execute(block);
        }

        public <B> Pattern2<A, B> join(DataSource<B> dataSource) {
            return rule.join(this, dataSource);
        }
    }

    public static class Pattern2<A, B> extends PatternDefinition<B, Pattern2<A, B>> {

        private final Pattern1<A> pattern1;

        public Pattern2(Pattern1<A> pattern1, UnitRule rule, Variable<B> variable) {
            super(rule, variable);
            this.pattern1 = pattern1;
        }

        public <V> Pattern2<A, B> filterJoin(Index.ConstraintType constraintType, Function1<A, B> rightExtractor) {
            return filterJoin("this", a -> a, constraintType, rightExtractor);
        }

        public <V> Pattern2<A, B> filterJoin(Function1<B, V> leftExtractor, Index.ConstraintType constraintType, Function1<A, V> rightExtractor) {
            return filterJoin(null, leftExtractor, constraintType, rightExtractor);
        }

        public <V> Pattern2<A, B> filterJoin(String fieldName, Function1<B, V> leftExtractor, Index.ConstraintType constraintType, Function1<A, V> rightExtractor) {
            constraints.add(new BetaConstraint<>(variable, fieldName, leftExtractor, constraintType, pattern1.variable, rightExtractor));
            return this;
        }

        public void execute(Block2<A, B> block) {
            rule.consequence = DSL.on(pattern1.variable, variable).execute(block);
        }

        public <G> void execute(G globalObject, Block3<G, A, B> block) {
            rule.consequence = DSL.on(rule.asGlobal(globalObject), pattern1.variable, variable).execute(block);
        }
    }

    public interface Constraint<A> {
        void addConstraintToPattern(PatternDSL.PatternDef<A> patternDef);
    }

    public static class AlphaConstraint<L, R> implements Constraint<L> {
        private final Variable<L> variable;
        private final String fieldName;
        private final Function1<L, R> extractor;
        private final Index.ConstraintType constraintType;
        private final R rightValue;

        public AlphaConstraint(Variable<L> variable, String fieldName, Function1<L, R> extractor, Index.ConstraintType constraintType, R rightValue) {
            this.variable = variable;
            this.fieldName = fieldName;
            this.extractor = extractor;
            this.constraintType = constraintType;
            this.rightValue = rightValue;
        }

        @Override
        public void addConstraintToPattern(PatternDSL.PatternDef<L> patternDef) {
            String exprId = fieldName != null ?
                    "expr:" + variable.getType().getCanonicalName() + ":" + fieldName + ":" + constraintType + ":" + rightValue :
                    UUID.randomUUID().toString();
            AlphaIndex alphaIndex = rightValue != null && fieldName != null ?
                    alphaIndexedBy( (Class<R>) rightValue.getClass(), constraintType, ClassIntrospectionCache.getFieldIndex(variable.getType(), fieldName), extractor, rightValue ) :
                    null;
            PatternDSL.ReactOn reactOn = fieldName != null ? PatternDSL.reactOn(fieldName) : null;
            patternDef.expr(exprId, p -> constraintType.asPredicate().test(extractor.apply(p), rightValue), alphaIndex, reactOn);
        }
    }

    public static class BetaConstraint<L, R, V> implements Constraint<L> {
        private final Variable<L> leftVariable;
        private final String fieldName;
        private final Function1<L, V> leftExtractor;
        private final Index.ConstraintType constraintType;
        private final Variable<R> rightVariable;
        private final Function1<R, V> rightExtractor;

        public BetaConstraint(Variable<L> leftVariable, String fieldName, Function1<L, V> leftExtractor, Index.ConstraintType constraintType, Variable<R> rightVariable, Function1<R, V> rightExtractor) {
            this.leftVariable = leftVariable;
            this.fieldName = fieldName;
            this.leftExtractor = leftExtractor;
            this.constraintType = constraintType;
            this.rightVariable = rightVariable;
            this.rightExtractor = rightExtractor;
        }

        @Override
        public void addConstraintToPattern(PatternDSL.PatternDef<L> patternDef) {
            String exprId = fieldName != null ?
                    "expr:" + leftVariable.getType().getCanonicalName() + ":" + fieldName + ":" + constraintType :
                    UUID.randomUUID().toString();
            BetaIndex betaIndex = fieldName != null ?
                    betaIndexedBy( (Class<V>) Object.class, constraintType, ClassIntrospectionCache.getFieldIndex(leftVariable.getType(), fieldName), leftExtractor, rightExtractor ) :
                    null;
            PatternDSL.ReactOn reactOn = fieldName != null ? PatternDSL.reactOn(fieldName) : null;
            patternDef.expr(exprId, rightVariable, (l, r) -> constraintType.asPredicate().test(leftExtractor.apply(l), rightExtractor.apply(r)), betaIndex, reactOn);
        }
    }

    private static class ClassIntrospectionCache {
        private static final Map<Class<?>, List<String>> propertiesMap = new HashMap<>();

        public static int getFieldIndex(Class<?> patternClass, String fieldName) {
            return propertiesMap.computeIfAbsent(patternClass, PropertyReactivityUtil::getAccessiblePropertiesIncludingNonGetterValueMethod).indexOf(fieldName);
        }
    }
}
