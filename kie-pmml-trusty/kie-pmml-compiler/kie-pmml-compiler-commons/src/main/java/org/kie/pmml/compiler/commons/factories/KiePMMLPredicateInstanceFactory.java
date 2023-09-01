package org.kie.pmml.compiler.commons.factories;

import java.util.List;
import java.util.stream.Collectors;

import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.False;
import org.dmg.pmml.Field;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.SimplePredicate;
import org.dmg.pmml.SimpleSetPredicate;
import org.dmg.pmml.True;
import org.kie.pmml.commons.model.predicates.KiePMMLPredicate;

import static org.kie.pmml.compiler.commons.factories.KiePMMLCompoundPredicateInstanceFactory.getKiePMMLCompoundPredicate;
import static org.kie.pmml.compiler.commons.factories.KiePMMLFalsePredicateInstanceFactory.getKiePMMLFalsePredicate;
import static org.kie.pmml.compiler.commons.factories.KiePMMLSimplePredicateInstanceFactory.getKiePMMLSimplePredicate;
import static org.kie.pmml.compiler.commons.factories.KiePMMLSimpleSetPredicateInstanceFactory.getKiePMMLSimpleSetPredicate;
import static org.kie.pmml.compiler.commons.factories.KiePMMLTruePredicateInstanceFactory.getKiePMMLTruePredicate;

/**
 * Facade for actual implementations
 */
public class KiePMMLPredicateInstanceFactory {

    private static final String PREDICATE_NOT_MANAGED = "Predicate %s not managed";

    private KiePMMLPredicateInstanceFactory() {
        // Avoid instantiation
    }

    public static List<KiePMMLPredicate> getKiePMMLPredicates(List<Predicate> predicates,
                                                              final List<Field<?>> fields) {
        return predicates.stream().map(predicate -> getKiePMMLPredicate(predicate, fields)).collect(Collectors.toList());
    }

    static KiePMMLPredicate getKiePMMLPredicate(final Predicate predicate,
                                                final List<Field<?>> fields) {
        if (predicate instanceof SimplePredicate) {
            return getKiePMMLSimplePredicate((SimplePredicate) predicate, fields);
        } else if (predicate instanceof SimpleSetPredicate) {
            return getKiePMMLSimpleSetPredicate((SimpleSetPredicate) predicate);
        } else if (predicate instanceof CompoundPredicate) {
            return getKiePMMLCompoundPredicate((CompoundPredicate) predicate, fields);
        } else if (predicate instanceof True) {
            return getKiePMMLTruePredicate((True) predicate);
        } else if (predicate instanceof False) {
            return getKiePMMLFalsePredicate((False) predicate);
        } else {
            throw new IllegalArgumentException(String.format(PREDICATE_NOT_MANAGED, predicate.getClass()));
        }
    }
}
