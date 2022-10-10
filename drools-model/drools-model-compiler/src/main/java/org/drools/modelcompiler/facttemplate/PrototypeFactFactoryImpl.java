package org.drools.modelcompiler.facttemplate;

import java.util.Map;

import org.drools.model.Prototype;
import org.drools.model.PrototypeFact;
import org.drools.model.PrototypeFactFactory;

public class PrototypeFactFactoryImpl implements PrototypeFactFactory {

    @Override
    public PrototypeFact createMapBasedFact(Prototype prototype) {
        return (PrototypeFact) FactFactory.createMapBasedFact(prototype);
    }

    @Override
    public PrototypeFact createMapBasedFact(Prototype prototype, Map<String, Object> valuesMap) {
        return (PrototypeFact) FactFactory.createMapBasedFact(prototype, valuesMap);
    }
}
