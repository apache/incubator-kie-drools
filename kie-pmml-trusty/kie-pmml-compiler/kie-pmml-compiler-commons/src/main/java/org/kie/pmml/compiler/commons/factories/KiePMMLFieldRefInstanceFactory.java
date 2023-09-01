package org.kie.pmml.compiler.commons.factories;

import org.dmg.pmml.FieldRef;
import org.kie.pmml.commons.model.expressions.KiePMMLFieldRef;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLFieldRef</code> instance
 * out of <code>FieldRef</code>s
 */
public class KiePMMLFieldRefInstanceFactory {

    private KiePMMLFieldRefInstanceFactory() {
        // Avoid instantiation
    }

    static KiePMMLFieldRef getKiePMMLFieldRef(final FieldRef fieldRef) {
        return new KiePMMLFieldRef(fieldRef.getField().getValue(),
                                   KiePMMLExtensionInstanceFactory.getKiePMMLExtensions(fieldRef.getExtensions()),
                                   fieldRef.getMapMissingTo());
    }
}
