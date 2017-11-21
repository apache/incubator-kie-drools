package org.drools.modelcompiler.resourcestest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.assertj.core.api.Assertions;
import org.drools.core.ClockType;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.reteoo.AlphaNode;
import org.drools.model.DSL;
import org.drools.model.Global;
import org.drools.model.Index.ConstraintType;
import org.drools.model.Model;
import org.drools.model.Query;
import org.drools.model.Query1Def;
import org.drools.model.Query2Def;
import org.drools.model.Rule;
import org.drools.model.TypeMetaData;
import org.drools.model.Variable;
import org.drools.model.WindowReference;
import org.drools.model.functions.accumulate.AbstractAccumulateFunction;
import org.drools.model.functions.accumulate.Sum;
import org.drools.model.impl.ModelImpl;
import org.drools.modelcompiler.builder.KieBaseBuilder;
import org.drools.modelcompiler.domain.Adult;
import org.drools.modelcompiler.domain.Child;
import org.drools.modelcompiler.domain.Man;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Relationship;
import org.drools.modelcompiler.domain.Result;
import org.drools.modelcompiler.domain.StockTick;
import org.drools.modelcompiler.domain.Toy;
import org.drools.modelcompiler.domain.Woman;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.time.SessionPseudoClock;

import static java.util.Arrays.asList;
import static org.drools.model.DSL.accumulate;
import static org.drools.model.DSL.and;
import static org.drools.model.DSL.average;
import static org.drools.model.DSL.bind;
import static org.drools.model.DSL.declarationOf;
import static org.drools.model.DSL.execute;
import static org.drools.model.DSL.executeScript;
import static org.drools.model.DSL.expr;
import static org.drools.model.DSL.forall;
import static org.drools.model.DSL.from;
import static org.drools.model.DSL.globalOf;
import static org.drools.model.DSL.input;
import static org.drools.model.DSL.not;
import static org.drools.model.DSL.on;
import static org.drools.model.DSL.or;
import static org.drools.model.DSL.query;
import static org.drools.model.DSL.rule;
import static org.drools.model.DSL.sum;
import static org.drools.model.DSL.min;
import static org.drools.model.DSL.max;
import static org.drools.model.DSL.count;
import static org.drools.model.DSL.type;
import static org.drools.model.DSL.valueOf;
import static org.drools.model.DSL.when;
import static org.drools.model.DSL.window;
import static org.drools.modelcompiler.BaseModelTest.getObjectsIntoList;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class FlowResourcesTest implements Model {

    @Override
    public List<Rule> getRules() {
        return rules;
    }

    @Override
    public List<Global> getGlobals() {
        return globals;
    }

    @Override
    public List<Query> getQueries() {
        return queries;
    }

    @Override
    public List<TypeMetaData> getTypeMetaDatas() {
        return typeMetaDatas;
    }

    List<Rule> rules = new ArrayList<>();

    List<Query> queries = new ArrayList<>();

    List<Global> globals = new ArrayList<>();

    List<WindowReference> windowReferences = new ArrayList<>();

    List<TypeMetaData> typeMetaDatas = new ArrayList<>();

    final org.drools.model.Global<java.util.Map> var_results = globalOf(type(java.util.Map.class),
                                                                        "org.drools.testcoverage.functional",
                                                                        "results");

    {
        rules.add(rule_TestCount());
        rules.add(rule_TestAverage());
        rules.add(rule_TestMin());
        rules.add(rule_TestMax());
        globals.add(var_results);
    }

    /** Rule name: TestCount */
    private org.drools.model.Rule rule_TestCount() {
        final org.drools.model.Variable<java.lang.Number> var_$pattern_Number$1$ = declarationOf(type(java.lang.Number.class),
                                                                                                 "$pattern_Number$1$");
        final org.drools.model.Variable<Double> var_$result = declarationOf(type(Double.class),
                                                                            "$result");
        final org.drools.model.Variable<org.drools.testcoverage.common.model.AggregableFact> var_$pattern_AggregableFact$1$ = declarationOf(type(org.drools.testcoverage.common.model.AggregableFact.class),
                                                                                                                                            "$pattern_AggregableFact$1$");
        final org.drools.model.Variable<Double> var_$value = declarationOf(type(Double.class),
                                                                           "$value");
        final org.drools.model.Variable<java.lang.Integer> var_$expr$3$ = declarationOf(type(java.lang.Integer.class),
                                                                                        "$expr$3$");
        org.drools.model.Rule rule = rule("org.drools.testcoverage.functional",
                                          "TestCount").build(bind(var_$value).as(var_$pattern_AggregableFact$1$,
                                                                                 (_this) -> _this.getValue())
                                                                     .reactOn("value"),
                                                             bind(var_$result).as(var_$pattern_Number$1$,
                                                                                  (_this) -> _this.doubleValue())
                                                                     .reactOn("doubleValue"),
                                                             accumulate(expr("$expr$2$",
                                                                             var_$pattern_AggregableFact$1$,
                                                                             (_this) -> _this.getValue() != 0).indexedBy(double.class,
                                                                                                                         org.drools.model.Index.ConstraintType.NOT_EQUAL,
                                                                                                                         0,
                                                                                                                         _this -> _this.getValue(),
                                                                                                                         0)
                                                                                .reactOn("value"),
                                                                        count(var_$value).as(var_$expr$3$)),
                                                             on(var_results,
                                                                var_$result).execute((results, $result) -> {
                                                                 Message msg = new Message();
                                                                 msg.setMessage(Double.toString($result));
                                                                 results.put("count",
                                                                             msg);
                                                             }));
        return rule;
    }

    /** Rule name: TestAverage */
    private org.drools.model.Rule rule_TestAverage() {
        final org.drools.model.Variable<java.lang.Number> var_$pattern_Number$2$ = declarationOf(type(java.lang.Number.class),
                                                                                                 "$pattern_Number$2$");
        final org.drools.model.Variable<Double> var_$result = declarationOf(type(Double.class),
                                                                            "$result");
        final org.drools.model.Variable<org.drools.testcoverage.common.model.AggregableFact> var_$pattern_AggregableFact$2$ = declarationOf(type(org.drools.testcoverage.common.model.AggregableFact.class),
                                                                                                                                            "$pattern_AggregableFact$2$");
        final org.drools.model.Variable<Double> var_$value = declarationOf(type(Double.class),
                                                                           "$value");
        final org.drools.model.Variable<java.lang.Double> var_$expr$4$ = declarationOf(type(java.lang.Double.class),
                                                                                       "$expr$4$");
        org.drools.model.Rule rule = rule("org.drools.testcoverage.functional",
                                          "TestAverage").build(bind(var_$value).as(var_$pattern_AggregableFact$2$,
                                                                                   (_this) -> _this.getValue())
                                                                       .reactOn("value"),
                                                               bind(var_$result).as(var_$pattern_Number$2$,
                                                                                    (_this) -> _this.doubleValue())
                                                                       .reactOn("doubleValue"),
                                                               accumulate(expr("$expr$2$",
                                                                               var_$pattern_AggregableFact$2$,
                                                                               (_this) -> _this.getValue() != 0).indexedBy(double.class,
                                                                                                                           org.drools.model.Index.ConstraintType.NOT_EQUAL,
                                                                                                                           0,
                                                                                                                           _this -> _this.getValue(),
                                                                                                                           0)
                                                                                  .reactOn("value"),
                                                                          average(var_$value).as(var_$expr$4$)),
                                                               on(var_results,
                                                                  var_$result).execute((results, $result) -> {
                                                                   Message msg = new Message();
                                                                   msg.setMessage(Double.toString($result));
                                                                   results.put("average",
                                                                               msg);
                                                               }));
        return rule;
    }

    /** Rule name: TestMin */
    private org.drools.model.Rule rule_TestMin() {
        final org.drools.model.Variable<java.lang.Number> var_$pattern_Number$3$ = declarationOf(type(java.lang.Number.class),
                                                                                                 "$pattern_Number$3$");
        final org.drools.model.Variable<Double> var_$result = declarationOf(type(Double.class),
                                                                            "$result");
        final org.drools.model.Variable<org.drools.testcoverage.common.model.AggregableFact> var_$pattern_AggregableFact$3$ = declarationOf(type(org.drools.testcoverage.common.model.AggregableFact.class),
                                                                                                                                            "$pattern_AggregableFact$3$");
        final org.drools.model.Variable<Double> var_$value = declarationOf(type(Double.class),
                                                                           "$value");
        final org.drools.model.Variable<java.lang.Double> var_$expr$5$ = declarationOf(type(java.lang.Double.class),
                                                                                       "$expr$5$");
        org.drools.model.Rule rule = rule("org.drools.testcoverage.functional",
                                          "TestMin").build(bind(var_$value).as(var_$pattern_AggregableFact$3$,
                                                                               (_this) -> _this.getValue())
                                                                   .reactOn("value"),
                                                           bind(var_$result).as(var_$pattern_Number$3$,
                                                                                (_this) -> _this.doubleValue())
                                                                   .reactOn("doubleValue"),
                                                           accumulate(expr("$expr$2$",
                                                                           var_$pattern_AggregableFact$3$,
                                                                           (_this) -> _this.getValue() != 0).indexedBy(double.class,
                                                                                                                       org.drools.model.Index.ConstraintType.NOT_EQUAL,
                                                                                                                       0,
                                                                                                                       _this -> _this.getValue(),
                                                                                                                       0)
                                                                              .reactOn("value"),
                                                                      min(var_$value).as(var_$expr$5$)),
                                                           on(var_results,
                                                              var_$result).execute((results, $result) -> {
                                                               Message msg = new Message();
                                                               msg.setMessage(Double.toString($result));
                                                               results.put("min",
                                                                           msg);
                                                           }));
        return rule;
    }

    /** Rule name: TestMax */
    private org.drools.model.Rule rule_TestMax() {
        final org.drools.model.Variable<java.lang.Number> var_$pattern_Number$4$ = declarationOf(type(java.lang.Number.class),
                                                                                                 "$pattern_Number$4$");
        final org.drools.model.Variable<Double> var_$result = declarationOf(type(Double.class),
                                                                            "$result");
        final org.drools.model.Variable<org.drools.testcoverage.common.model.AggregableFact> var_$pattern_AggregableFact$4$ = declarationOf(type(org.drools.testcoverage.common.model.AggregableFact.class),
                                                                                                                                            "$pattern_AggregableFact$4$");
        final org.drools.model.Variable<Double> var_$value = declarationOf(type(Double.class),
                                                                           "$value");
        final org.drools.model.Variable<java.lang.Double> var_$expr$6$ = declarationOf(type(java.lang.Double.class),
                                                                                       "$expr$6$");
        org.drools.model.Rule rule = rule("org.drools.testcoverage.functional",
                                          "TestMax").build(bind(var_$value).as(var_$pattern_AggregableFact$4$,
                                                                               (_this) -> _this.getValue())
                                                                   .reactOn("value"),
                                                           bind(var_$result).as(var_$pattern_Number$4$,
                                                                                (_this) -> _this.doubleValue())
                                                                   .reactOn("doubleValue"),
                                                           accumulate(expr("$expr$2$",
                                                                           var_$pattern_AggregableFact$4$,
                                                                           (_this) -> _this.getValue() != 0).indexedBy(double.class,
                                                                                                                       org.drools.model.Index.ConstraintType.NOT_EQUAL,
                                                                                                                       0,
                                                                                                                       _this -> _this.getValue(),
                                                                                                                       0)
                                                                              .reactOn("value"),
                                                                      max(var_$value).as(var_$expr$6$)),
                                                           on(var_results,
                                                              var_$result).execute((results, $result) -> {
                                                               Message msg = new Message();
                                                               msg.setMessage(Double.toString($result));
                                                               results.put("max",
                                                                           msg);
                                                           }));
        return rule;
    }
}

