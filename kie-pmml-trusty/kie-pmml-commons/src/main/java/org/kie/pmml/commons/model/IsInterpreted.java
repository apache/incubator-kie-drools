package org.kie.pmml.commons.model;

/**
 * Marker interface used to define if a given <code>KiePMMLModel</code> implements the interpreted version.
 * This is used, at codegen-time, to generate the correct instantiation inside <code>KiePMMLFactoryFactory.getInstantiationExpression</code>
 */
public interface IsInterpreted {

}
