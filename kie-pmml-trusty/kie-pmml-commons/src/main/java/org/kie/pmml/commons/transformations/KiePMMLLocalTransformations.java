package org.kie.pmml.commons.transformations;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/Transformations.html>LocalTransformations</a>
 */
public class KiePMMLLocalTransformations extends AbstractKiePMMLComponent implements Serializable {

    private static final long serialVersionUID = 9187889880911645935L;

    private List<KiePMMLDerivedField> derivedFields;

    private KiePMMLLocalTransformations(String name, List<KiePMMLExtension> extensions) {
        super(name, extensions);
    }

    public static Builder builder(String name, List<KiePMMLExtension> extensions) {
        return new Builder(name, extensions);
    }

    public List<KiePMMLDerivedField> getDerivedFields() {
        return derivedFields != null ? Collections.unmodifiableList(derivedFields) : Collections.emptyList();
    }

    public static class Builder extends AbstractKiePMMLComponent.Builder<KiePMMLLocalTransformations> {

        private Builder(String name, List<KiePMMLExtension> extensions) {
            super("LocalTransformations-", () -> new KiePMMLLocalTransformations(name, extensions));
        }

        public Builder withDerivedFields(List<KiePMMLDerivedField> derivedFields) {
            if (derivedFields != null) {
                toBuild.derivedFields = derivedFields;
            }
            return this;
        }
    }


}
