package org.kie.pmml.commons.transformations;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/Transformations.html>TransformationDictionary</a>
 */
public class KiePMMLTransformationDictionary extends AbstractKiePMMLComponent implements Serializable {

    private static final long serialVersionUID = 9187889880911645935L;

    private List<KiePMMLDefineFunction> defineFunctions;
    private List<KiePMMLDerivedField> derivedFields;

    private KiePMMLTransformationDictionary(String name, List<KiePMMLExtension> extensions) {
        super(name, extensions);
    }

    public static Builder builder(String name, List<KiePMMLExtension> extensions) {
        return new Builder(name, extensions);
    }

    public List<KiePMMLDefineFunction> getDefineFunctions() {
        return defineFunctions != null ? Collections.unmodifiableList(defineFunctions) : Collections.emptyList();
    }

    public List<KiePMMLDerivedField> getDerivedFields() {
        return derivedFields != null ? Collections.unmodifiableList(derivedFields) : Collections.emptyList();
    }

    public static class Builder extends AbstractKiePMMLComponent.Builder<KiePMMLTransformationDictionary> {

        private Builder(String name, List<KiePMMLExtension> extensions) {
            super("TransformationDictionary-", () -> new KiePMMLTransformationDictionary(name, extensions));
        }

        public Builder withDefineFunctions(List<KiePMMLDefineFunction> defineFunctions) {
            if (defineFunctions != null) {
                toBuild.defineFunctions = defineFunctions;
            }
            return this;
        }

        public Builder withDerivedFields(List<KiePMMLDerivedField> derivedFields) {
            if (derivedFields != null) {
                toBuild.derivedFields = derivedFields;
            }
            return this;
        }
    }
}
