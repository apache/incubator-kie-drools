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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.drools.model.DSL;
import org.drools.model.Global;
import org.drools.model.Index;
import org.drools.model.Model;
import org.drools.model.PatternDSL;
import org.drools.model.Rule;
import org.drools.model.RuleItemBuilder;
import org.drools.model.Variable;
import org.drools.model.functions.Block1;
import org.drools.model.functions.Function1;
import org.drools.model.impl.ModelImpl;
import org.drools.model.impl.RuleBuilder;
import org.drools.ruleunits.api.DataSource;

import static org.drools.model.DSL.declarationOf;
import static org.drools.model.DSL.entryPoint;
import static org.drools.model.DSL.globalOf;
import static org.drools.model.PatternDSL.alphaIndexedBy;
import static org.drools.model.PatternDSL.pattern;
import static org.drools.model.PatternDSL.rule;

public class RulesContext {

    private final RuleUnitDefinition unit;

    private final List<UnitRule> rules = new ArrayList<>();
    private final Map<Object, Global> globals = new IdentityHashMap<>();
    private final Map<Object, Variable> dsIds = new IdentityHashMap<>();

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

        private final Map<Variable, List<Constraint>> constraints = new HashMap<>();
        private Consequence consequence;

        public UnitRule(RulesContext context, String name) {
            this.context = context;
            this.name = name;
        }

        public <T> DS1<T> on(DataSource<T> dataSource, Class<?> dataClass) {
            Global global = context.globals.computeIfAbsent(dataSource, o -> globalOf(o.getClass(), context.unit.getClass().getCanonicalName(), UUID.randomUUID().toString()));
            return new DS1<>(this, context.dsIds.computeIfAbsent(dataSource, o -> declarationOf(dataClass, entryPoint(global.getName()))));
        }

        public <T> G1<T> on(T globalObject) {
            Global global = context.globals.computeIfAbsent(globalObject, o -> globalOf(o.getClass(), context.unit.getClass().getCanonicalName(), UUID.randomUUID().toString()));
            return new G1<>(this, global);
        }

        public void addConstraint(Constraint constraint) {
            constraints.computeIfAbsent(constraint.getVariable(), id -> new ArrayList<>()).add(constraint);
        }

        public Rule toRule() {
            RuleBuilder ruleBuilder = rule(context.unit.getClass().getCanonicalName(), name).unit(context.unit.getClass());

            List<RuleItemBuilder> items = new ArrayList<>();

            for (Map.Entry<Variable, List<Constraint>> entry : constraints.entrySet()) {
                PatternDSL.PatternDef patternDef = pattern(entry.getKey());
                for (Constraint constraint : entry.getValue()) {
                    constraint.addConstraintToPattern(patternDef);
                }
                items.add(patternDef);
            }

            if (consequence != null) {
                items.add(consequence.toConsequence());
            }

            return ruleBuilder.build(items.toArray(new RuleItemBuilder[items.size()]));
        }
    }

    public static class DS1<T> {

        private final UnitRule rule;
        private final Variable dsVar;

        public DS1(UnitRule rule, Variable dsVar) {
            this.rule = rule;
            this.dsVar = dsVar;
        }

        public UnitRule check(Index.ConstraintType constraintType, T rightValue) {
            return check(t -> t, constraintType, rightValue);
        }

        public <V> UnitRule check(Function1<T, V> extractor, Index.ConstraintType constraintType, V rightValue) {
            rule.addConstraint(new DS1Constraint<>(dsVar, extractor, constraintType, rightValue));
            return rule;
        }

        public void execute(Block1<T> consequence) {
        }
    }

    public static class G1<T> {

        private final UnitRule rule;
        private final Global global;

        public G1(UnitRule rule, Global global) {
            this.rule = rule;
            this.global = global;
        }

        public void execute(Block1<T> block) {
            rule.consequence = new C1OnG1(global, block);
        }
    }

    public interface Constraint<T> {
        Variable<T> getVariable();
        void addConstraintToPattern(PatternDSL.PatternDef<T> patternDef);
    }

    public static class DS1Constraint<T, V> implements Constraint<T> {
        private final Variable<T> dsVar;
        private final Function1<T, V> extractor;
        private final Index.ConstraintType constraintType;
        private final V rightValue;

        public DS1Constraint(Variable<T> dsVar, Function1<T, V> extractor, Index.ConstraintType constraintType, V rightValue) {
            this.dsVar = dsVar;
            this.extractor = extractor;
            this.constraintType = constraintType;
            this.rightValue = rightValue;
        }

        @Override
        public Variable<T> getVariable() {
            return dsVar;
        }

        @Override
        public void addConstraintToPattern(PatternDSL.PatternDef<T> patternDef) {
            patternDef.expr("expr:" + dsVar.getName() + ":" + constraintType + ":" + rightValue,
                    p -> constraintType.asPredicate().test(extractor.apply(p), rightValue),
                    alphaIndexedBy( (Class<V>) rightValue.getClass(), constraintType, -1, extractor, rightValue ));
        }
    }

    public interface Consequence {
        RuleItemBuilder toConsequence();
    }

    public static class C1OnG1 implements Consequence {
        private final Global global;
        private final Block1 block;

        public C1OnG1(Global global, Block1 block) {
            this.global = global;
            this.block = block;
        }

        @Override
        public RuleItemBuilder toConsequence() {
            return DSL.on(global).execute(block);
        }
    }
}
