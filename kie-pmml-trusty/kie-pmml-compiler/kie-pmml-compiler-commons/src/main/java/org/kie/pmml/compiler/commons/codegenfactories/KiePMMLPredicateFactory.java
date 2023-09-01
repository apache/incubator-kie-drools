package org.kie.pmml.compiler.commons.codegenfactories;

import java.util.List;

import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.False;
import org.dmg.pmml.Field;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.SimplePredicate;
import org.dmg.pmml.SimpleSetPredicate;
import org.dmg.pmml.True;

import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLCompoundPredicateFactory.getCompoundPredicateVariableDeclaration;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLFalsePredicateFactory.getFalsePredicateVariableDeclaration;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLSimplePredicateFactory.getSimplePredicateVariableDeclaration;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLSimpleSetPredicateFactory.getSimpleSetPredicateVariableDeclaration;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLTruePredicateFactory.getTruePredicateVariableDeclaration;

/**
 * Facade for actual implementations
 */
public class KiePMMLPredicateFactory {

    private static final String PREDICATE_NOT_MANAGED = "Predicate %s not managed";

    private KiePMMLPredicateFactory() {
        // Avoid instantiation
    }

    public static BlockStmt getKiePMMLPredicate(final String variableName,
                                                final Predicate predicate,
                                                final List<Field<?>> fields) {
        if (predicate instanceof SimplePredicate) {
            return getSimplePredicateVariableDeclaration(variableName, (SimplePredicate) predicate, fields);
        } else if (predicate instanceof SimpleSetPredicate) {
            return getSimpleSetPredicateVariableDeclaration(variableName, (SimpleSetPredicate) predicate);
        } else if (predicate instanceof CompoundPredicate) {
            return getCompoundPredicateVariableDeclaration(variableName, (CompoundPredicate) predicate, fields);
        } else if (predicate instanceof True) {
            return getTruePredicateVariableDeclaration(variableName, (True) predicate);
        } else if (predicate instanceof False) {
            return getFalsePredicateVariableDeclaration(variableName, (False) predicate);
        } else {
            throw new IllegalArgumentException(String.format(PREDICATE_NOT_MANAGED, predicate.getClass()));
        }
    }

}
