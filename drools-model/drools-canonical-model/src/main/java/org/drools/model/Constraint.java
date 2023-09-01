package org.drools.model;

import java.util.List;

import org.drools.model.functions.PredicateInformation;

public interface Constraint {

    enum Type { SINGLE, MULTIPLE, OR, AND }

    List<Constraint> getChildren();

    Type getType();

    Constraint negate();

    Constraint replaceVariable( Variable oldVar, Variable newVar );

    default PredicateInformation predicateInformation() { return PredicateInformation.EMPTY_PREDICATE_INFORMATION; }
}
