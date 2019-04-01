package org.kie.submarine.rules;

public interface RuleUnit<T> {
    RuleUnitInstance<T> createInstance(T workingMemory);
}
