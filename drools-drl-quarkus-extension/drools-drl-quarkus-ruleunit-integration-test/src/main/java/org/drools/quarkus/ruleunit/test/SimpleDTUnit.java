package org.drools.quarkus.ruleunit.test;

import java.util.concurrent.atomic.AtomicReference;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.SingletonStore;
import org.drools.ruleunits.dsl.RuleUnitDefinition;
import org.drools.ruleunits.dsl.RulesFactory;

import static org.drools.model.Index.ConstraintType.EQUAL;
import static org.drools.model.Index.ConstraintType.GREATER_OR_EQUAL;
import static org.drools.model.Index.ConstraintType.LESS_THAN;

public class SimpleDTUnit implements RuleUnitDefinition {
    
    private SingletonStore<Number> age;
    private SingletonStore<Boolean> incidents;
    
    private AtomicReference<Number> basePrice;

    @Override
    public void defineRules(RulesFactory rulesFactory) {
        rulesFactory.rule()
                    .on(age)
                    .filter(LESS_THAN, 21) // when no extractor is provided "this" is implicit
                    .join(rule -> rule.on(incidents).filter(EQUAL, false))
                    .execute(basePrice, (r, c1, c2) -> r.set(800));
        rulesFactory.rule()
                    .on(age)
                    .filter(LESS_THAN, 21) 
                    .join(rule -> rule.on(incidents).filter(EQUAL, true))
                    .execute(basePrice, (r, c1, c2) -> r.set(1000));
        rulesFactory.rule()
                    .on(age)
                    .filter(GREATER_OR_EQUAL, 21)
                    .join(rule -> rule.on(incidents).filter(EQUAL, false))
                    .execute(basePrice, (r, c1, c2) -> r.set(500));
        rulesFactory.rule()
                    .on(age)
                    .filter(GREATER_OR_EQUAL, 21) 
                    .join(rule -> rule.on(incidents).filter(EQUAL, true))
                    .execute(basePrice, (r, c1, c2) -> r.set(600));
    }

    public SimpleDTUnit() {
        this(DataSource.createSingleton(), DataSource.createSingleton(), null);
    }

    public SimpleDTUnit(SingletonStore<Number> age, SingletonStore<Boolean> incidents, Number basePrice) {
        this.age = age;
        this.incidents = incidents;
        this.basePrice = new AtomicReference<>(basePrice);
    }

    public SingletonStore<Number> getAge() {
        return age;
    }

    public SingletonStore<Boolean> getIncidents() {
        return incidents;
    }

    public AtomicReference<Number> getBasePrice() {
        return basePrice;
    }

    public void setAge(SingletonStore<Number> age) {
        this.age = age;
    }

    public void setIncidents(SingletonStore<Boolean> incidents) {
        this.incidents = incidents;
    }

    public void setBasePrice(AtomicReference<Number> basePrice) {
        this.basePrice = basePrice;
    }
}