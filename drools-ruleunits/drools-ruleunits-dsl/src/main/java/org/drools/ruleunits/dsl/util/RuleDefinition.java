package org.drools.ruleunits.dsl.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.drools.model.DSL;
import org.drools.model.Global;
import org.drools.model.Rule;
import org.drools.model.RuleItemBuilder;
import org.drools.model.functions.Block1;
import org.drools.model.impl.RuleBuilder;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.dsl.RuleUnitDefinition;
import org.drools.ruleunits.dsl.RulesFactory;
import org.drools.ruleunits.dsl.accumulate.AccumulatePattern1;
import org.drools.ruleunits.dsl.accumulate.Accumulator1;
import org.drools.ruleunits.dsl.patterns.Pattern1;
import org.drools.ruleunits.dsl.patterns.PatternDefinition;

import static org.drools.model.DSL.declarationOf;
import static org.drools.model.DSL.entryPoint;
import static org.drools.model.PatternDSL.rule;

public class RuleDefinition {
    private final RuleUnitDefinition unit;
    private final RulesFactory.UnitGlobals globals;

    private final List<PatternDefinition> patterns = new ArrayList<>();
    private RuleItemBuilder consequence;

    public RuleDefinition(RuleUnitDefinition unit, RulesFactory.UnitGlobals globals) {
        this.globals = globals;
        this.unit = unit;
    }

    public void addPattern(PatternDefinition pattern) {
        patterns.add(pattern);
    }

    public void removePattern(PatternDefinition pattern) {
        patterns.remove(pattern);
    }

    public <A> Pattern1<A> from(DataSource<A> dataSource) {
        Pattern1<A> pattern1 = new Pattern1<>(this, declarationOf(findDataSourceClass(dataSource),
                entryPoint(asGlobal(dataSource).getName())));
        addPattern(pattern1);
        return pattern1;
    }

    public <A, B> Pattern1<B> accumulate(Pattern1<A> pattern, Accumulator1<A, B> acc) {
        removePattern(pattern);
        Pattern1<B> accPattern = new AccumulatePattern1<>(this, pattern, acc);
        addPattern(accPattern);
        return accPattern;
    }

    public void setConsequence(RuleItemBuilder consequence) {
        this.consequence = consequence;
    }

    public <T> void setConsequence(T globalObject, Block1<T> block) {
        this.consequence = DSL.on(asGlobal(globalObject)).execute(block);
    }

    public <T> Global asGlobal(T globalObject) {
        return globals.asGlobal(globalObject);
    }

    public Rule toRule(String name) {
        RuleBuilder ruleBuilder = rule(unit.getClass().getCanonicalName(), name).unit(unit.getClass());

        List<RuleItemBuilder> items = new ArrayList<>();

        for (PatternDefinition<?> pattern : patterns) {
            items.add(pattern.toExecModelItem());
        }

        if (consequence != null) {
            items.add(consequence);
        }

        return ruleBuilder.build(items.toArray(new RuleItemBuilder[items.size()]));
    }

    private <A> Class<A> findDataSourceClass(DataSource<A> dataSource) {
        assert(dataSource != null);
        for (Field field : unit.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if (dataSource == field.get(unit)) {
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
}