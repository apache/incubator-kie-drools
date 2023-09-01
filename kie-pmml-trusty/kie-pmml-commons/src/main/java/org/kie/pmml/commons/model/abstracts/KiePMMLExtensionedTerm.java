package org.kie.pmml.commons.model.abstracts;

import java.util.List;

import org.kie.pmml.commons.model.KiePMMLExtension;

public abstract class KiePMMLExtensionedTerm extends AbstractKiePMMLComponent {

    public KiePMMLExtensionedTerm(String name, List<KiePMMLExtension> extensions) {
        super(name, extensions);
    }

    public abstract Number getCoefficient();
}
