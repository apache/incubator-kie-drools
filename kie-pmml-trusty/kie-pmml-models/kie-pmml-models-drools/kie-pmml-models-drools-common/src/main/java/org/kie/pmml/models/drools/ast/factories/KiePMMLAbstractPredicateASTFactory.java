package org.kie.pmml.models.drools.ast.factories;

/**
 * Abstract class to be extended to generate <code>KiePMMLDroolsRule</code>s out of a <code>Predicate</code>s
 */
public class KiePMMLAbstractPredicateASTFactory {

    protected final PredicateASTFactoryData predicateASTFactoryData;

    protected KiePMMLAbstractPredicateASTFactory(final PredicateASTFactoryData predicateASTFactoryData) {
        this.predicateASTFactoryData = predicateASTFactoryData;
    }
}
