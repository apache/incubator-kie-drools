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
package org.drools.ruleunits.dsl.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import org.drools.model.Condition;
import org.drools.model.DSL;
import org.drools.model.Global;
import org.drools.model.Rule;
import org.drools.model.RuleItemBuilder;
import org.drools.model.functions.Block1;
import org.drools.model.functions.Function1;
import org.drools.model.impl.RuleBuilder;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.dsl.RuleFactory;
import org.drools.ruleunits.dsl.RuleUnitDefinition;
import org.drools.ruleunits.dsl.RulesFactory;
import org.drools.ruleunits.dsl.SyntheticRuleUnit;
import org.drools.ruleunits.dsl.accumulate.AccumulatePattern1;
import org.drools.ruleunits.dsl.accumulate.Accumulator1;
import org.drools.ruleunits.dsl.accumulate.GroupByPattern1;
import org.drools.ruleunits.dsl.patterns.CombinedPatternDef;
import org.drools.ruleunits.dsl.patterns.ExistentialPatternDef;
import org.drools.ruleunits.dsl.patterns.InternalPatternDef;
import org.drools.ruleunits.dsl.patterns.Pattern1DefImpl;
import org.drools.ruleunits.dsl.patterns.Pattern2Def;
import org.drools.ruleunits.dsl.patterns.Pattern2DefImpl;
import org.drools.ruleunits.dsl.patterns.PatternDef;
import org.drools.ruleunits.impl.datasources.ConsequenceDataStore;
import org.drools.ruleunits.impl.datasources.ConsequenceDataStoreImpl;
import org.kie.api.runtime.rule.RuleContext;

import static org.drools.model.DSL.declarationOf;
import static org.drools.model.DSL.entryPoint;
import static org.drools.model.PatternDSL.rule;

public class RuleDefinition implements RuleFactory {

    private final String name;
    private final RuleUnitDefinition unit;
    private final RulesFactory.UnitGlobals globals;

    private final List<InternalPatternDef> patterns = new ArrayList<>();
    private RuleItemBuilder consequence;

    private boolean registerNewPattern = true;

    public RuleDefinition(String name, RuleUnitDefinition unit, RulesFactory.UnitGlobals globals) {
        this.name = name;
        this.globals = globals;
        this.unit = unit;
    }

    public void addPattern(InternalPatternDef pattern) {
        patterns.add(pattern);
    }

    public <B> InternalPatternDef internalCreatePattern(B builder, Function1<B, PatternDef> patternBuilder) {
        registerNewPattern = false;
        try {
            InternalPatternDef created = (InternalPatternDef) patternBuilder.apply(builder);
            return builder instanceof InternalPatternDef ? created.subPatternFrom( (InternalPatternDef)builder ) : created;
        } finally {
            registerNewPattern = true;
        }
    }

    @Override
    public <A> Pattern1DefImpl<A> on(DataSource<A> dataSource) {
        DataSourceFieldDefinition dataSourceField = (DataSourceFieldDefinition) findUnitField(dataSource);
        Pattern1DefImpl<A> pattern1 = new Pattern1DefImpl<>(this,
                declarationOf(dataSourceField.getDataSourceClass(), entryPoint(asGlobal(() -> dataSourceField, dataSource).getName())));
        if (registerNewPattern) {
            addPattern(pattern1);
        }
        return pattern1;
    }

    @Override
    public RuleFactory not(Function1<RuleFactory, PatternDef> patternBuilder) {
        addPattern(new ExistentialPatternDef(Condition.Type.NOT, internalCreatePattern(this, patternBuilder)));
        return this;
    }

    @Override
    public RuleFactory exists(Function1<RuleFactory, PatternDef> patternBuilder) {
        addPattern(new ExistentialPatternDef(Condition.Type.EXISTS, internalCreatePattern(this, patternBuilder)));
        return this;
    }

    @Override
    public <A, B> Pattern1DefImpl<B> accumulate(Function1<RuleFactory, PatternDef> patternBuilder, Accumulator1<A, B> acc) {
        Pattern1DefImpl accPattern = asAccumulatePattern(internalCreatePattern(this, patternBuilder), acc);
        addPattern(accPattern);
        return accPattern;
    }

    private Pattern1DefImpl asAccumulatePattern(InternalPatternDef patternDef, Accumulator1 acc) {
        if (patternDef instanceof Pattern1DefImpl) {
            return new AccumulatePattern1<>(this, patternDef, acc);
        }
        if (patternDef instanceof Pattern2DefImpl) {
            Pattern2DefImpl pattern = (Pattern2DefImpl) patternDef;
            return new AccumulatePattern1<>(this, new CombinedPatternDef(Condition.Type.AND, pattern.getPatternA(), pattern.getPatternB()), acc);
        }
        throw new UnsupportedOperationException();
    }


    @Override
    public <A, K, V> Pattern2Def<K, V> groupBy(Function1<RuleFactory, PatternDef> patternBuilder, Function1<A, K> groupingFunction, Accumulator1<A, V> acc) {
        GroupByPattern1 groupByPattern = new GroupByPattern1(this, internalCreatePattern(this, patternBuilder), groupingFunction, acc);
        addPattern(groupByPattern);
        return groupByPattern;
    }

    public void setConsequence(RuleItemBuilder consequence) {
        this.consequence = consequence;
    }

    @Override
    public <T> void execute(T globalObject, Block1<T> block) {
        this.consequence = DSL.on(asGlobal(globalObject)).execute(block);
    }

    @Override
    public <T> void executeOnDataStore(DataStore<T> dataStore, Block1<ConsequenceDataStore<T>> block) {
        this.consequence = DSL.on(asGlobal(dataStore)).execute( (drools, ds) -> block.execute(new ConsequenceDataStoreImpl<>((RuleContext) drools, (DataStore<T>) ds)) );
    }

    public <T> Global asGlobal(T globalObject) {
        return asGlobal(() -> findUnitField(globalObject), globalObject);
    }

    public <T> Global asGlobal(Supplier<FieldDefinition> globalField, T globalObject) {
        return globals.asGlobal(globalField, globalObject);
    }

    public Rule toRule() {
        RuleBuilder ruleBuilder = rule(unit.getClass().getCanonicalName(), name).unit(unit.getClass());

        List<RuleItemBuilder> items = new ArrayList<>();

        for (InternalPatternDef pattern : patterns) {
            items.add(pattern.toExecModelItem());
        }

        if (consequence != null) {
            items.add(consequence);
        }

        return ruleBuilder.build(items.toArray(new RuleItemBuilder[items.size()]));
    }

    private FieldDefinition findUnitField(Object object) {
        Objects.requireNonNull(object);

        if (unit instanceof SyntheticRuleUnit) {
            return findSyntheticUnitField(object, (SyntheticRuleUnit) unit);
        }

        for (Field field : unit.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if (object == field.get(unit)) {
                    return new ReflectiveFieldDefinition(field);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalArgumentException("Unknown unit field for object: " + object);
    }

    private FieldDefinition findSyntheticUnitField(Object object, SyntheticRuleUnit syntheticRuleUnit) {
        for (Map.Entry<String, DataSourceDefinition> entry : syntheticRuleUnit.getDataSourceDefinitions().entrySet()) {
            if (object == entry.getValue().getDataSource()) {
                return new SyntheticDataSourceFieldDefinition(entry.getKey(), entry.getValue());
            }
        }
        for (Map.Entry<String, Object> entry : syntheticRuleUnit.getGlobals().entrySet()) {
            if (object == entry.getValue()) {
                return new SyntheticGlobalFieldDefinition(entry.getKey());
            }
        }
        throw new IllegalArgumentException("Unknown unit field for object: " + object);
    }

    public interface FieldDefinition {
        Object get(RuleUnitData unit);
    }

    public interface DataSourceFieldDefinition extends FieldDefinition {
        <A> Class<A> getDataSourceClass();
    }

    static class ReflectiveFieldDefinition implements DataSourceFieldDefinition {
        private final Field field;

        ReflectiveFieldDefinition(Field field) {
            this.field = field;
        }

        @Override
        public Object get(RuleUnitData unit) {
            try {
                return field.get(unit);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public <A> Class<A> getDataSourceClass() {
            Type dsType = field.getGenericType();
            if (dsType instanceof ParameterizedType) {
                return (Class<A>) ((ParameterizedType) dsType).getActualTypeArguments()[0];
            }
            throw new IllegalArgumentException("Unknown DataSource type");
        }
    }

    static class SyntheticDataSourceFieldDefinition implements DataSourceFieldDefinition {

        private final String name;
        private final DataSourceDefinition dataSourceDefinition;

        SyntheticDataSourceFieldDefinition(String name, DataSourceDefinition dataSourceDefinition) {
            this.name = name;
            this.dataSourceDefinition = dataSourceDefinition;
        }

        @Override
        public Object get(RuleUnitData unit) {
            return ((SyntheticRuleUnit) unit).getDataSourceDefinitions().get(name).getDataSource();
        }

        @Override
        public <A> Class<A> getDataSourceClass() {
            return (Class<A>) dataSourceDefinition.getDataClass();
        }
    }

    static class SyntheticGlobalFieldDefinition implements FieldDefinition {

        private final String name;

        SyntheticGlobalFieldDefinition(String name) {
            this.name = name;
        }

        @Override
        public Object get(RuleUnitData unit) {
            return ((SyntheticRuleUnit) unit).getGlobals().get(name);
        }
    }
}