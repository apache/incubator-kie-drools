package org.drools.ruleunits.dsl.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.drools.model.Condition;
import org.drools.model.DSL;
import org.drools.model.Global;
import org.drools.model.Rule;
import org.drools.model.RuleItemBuilder;
import org.drools.model.functions.Block1;
import org.drools.model.functions.Function1;
import org.drools.model.impl.RuleBuilder;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.dsl.RuleFactory;
import org.drools.ruleunits.dsl.RuleUnitDefinition;
import org.drools.ruleunits.dsl.RulesFactory;
import org.drools.ruleunits.dsl.accumulate.AccumulatePattern1;
import org.drools.ruleunits.dsl.accumulate.Accumulator1;
import org.drools.ruleunits.dsl.patterns.CombinedPatternDef;
import org.drools.ruleunits.dsl.patterns.Pattern1Def;
import org.drools.ruleunits.dsl.patterns.Pattern2Def;
import org.drools.ruleunits.dsl.patterns.SinglePatternDef;

import static org.drools.model.DSL.declarationOf;
import static org.drools.model.DSL.entryPoint;
import static org.drools.model.PatternDSL.rule;

public class RuleDefinition implements RuleFactory {

    private final String name;
    private final RuleUnitDefinition unit;
    private final RulesFactory.UnitGlobals globals;

    private final List<SinglePatternDef> patterns = new ArrayList<>();
    private RuleItemBuilder consequence;

    public RuleDefinition(String name, RuleUnitDefinition unit, RulesFactory.UnitGlobals globals) {
        this.name = name;
        this.globals = globals;
        this.unit = unit;
    }

    public void addPattern(SinglePatternDef pattern) {
        patterns.add(pattern);
    }

    public void removePattern(SinglePatternDef pattern) {
        patterns.remove(pattern);
    }

    @Override
    public <A> Pattern1Def<A> from(DataSource<A> dataSource) {
        Pattern1Def<A> pattern1 = new Pattern1Def<>(this, declarationOf(findDataSourceClass(dataSource),
                entryPoint(asGlobal(dataSource).getName())));
        addPattern(pattern1);
        return pattern1;
    }

    @Override
    public <A, B> Pattern1Def<B> accumulate(Function1<RuleFactory, SinglePatternDef<A>> patternBuilder, Accumulator1<A, B> acc) {
        SinglePatternDef patternDef = patternBuilder.apply(this);
        if (patternDef instanceof Pattern1Def) {
            return accumulate(((Pattern1Def) patternDef), acc);
        }
        if (patternDef instanceof Pattern2Def) {
            return accumulate(((Pattern2Def) patternDef), acc);
        }
        throw new UnsupportedOperationException();
    }

    public Pattern1Def accumulate(Pattern1Def pattern, Accumulator1 acc) {
        removePattern(pattern);
        Pattern1Def accPattern = new AccumulatePattern1<>(this, pattern, acc);
        addPattern(accPattern);
        return accPattern;
    }

    public Pattern1Def accumulate(Pattern2Def pattern, Accumulator1 acc) {
        removePattern(pattern.getPatternA());
        removePattern(pattern.getPatternB());
        Pattern1Def accPattern = new AccumulatePattern1<>(this, new CombinedPatternDef(Condition.Type.AND, pattern.getPatternA(), pattern.getPatternB()), acc);
        addPattern(accPattern);
        return accPattern;
    }

    public void setConsequence(RuleItemBuilder consequence) {
        this.consequence = consequence;
    }

    @Override
    public <T> void execute(T globalObject, Block1<T> block) {
        this.consequence = DSL.on(asGlobal(globalObject)).execute(block);
    }

    public <T> Global asGlobal(T globalObject) {
        return globals.asGlobal(globalObject);
    }

    public Rule toRule() {
        RuleBuilder ruleBuilder = rule(unit.getClass().getCanonicalName(), name).unit(unit.getClass());

        List<RuleItemBuilder> items = new ArrayList<>();

        for (SinglePatternDef<?> pattern : patterns) {
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