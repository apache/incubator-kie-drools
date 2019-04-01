package org.kie.submarine.rules;

public interface RuleUnitInstance<T> {
    RuleUnit<T> unit();
    void fire();
}
