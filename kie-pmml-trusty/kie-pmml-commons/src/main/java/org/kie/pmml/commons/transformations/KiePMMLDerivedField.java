package org.kie.pmml.commons.transformations;

import java.io.Serializable;
import java.util.List;

import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.ProcessingDTO;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;
import org.kie.pmml.commons.model.expressions.KiePMMLExpression;

import static org.kie.pmml.commons.utils.KiePMMLModelUtils.commonEvaluate;

/**
 * @see <a href=http://dmg.org/pmml/v4-4-1/Transformations.html#xsdElement_DerivedField>DerivedField</a>
 */
public class KiePMMLDerivedField extends AbstractKiePMMLComponent implements Serializable {

    private static final long serialVersionUID = 9187889880911645935L;

    private final DATA_TYPE dataType;
    private final OP_TYPE opType;
    private final KiePMMLExpression kiePMMLExpression;
    private String displayName;

    private KiePMMLDerivedField(String name,
                               List<KiePMMLExtension> extensions,
                                DATA_TYPE dataType,
                                OP_TYPE opType,
                               KiePMMLExpression kiePMMLExpression) {
        super(name, extensions);
        this.dataType = dataType;
        this.opType = opType;
        this.kiePMMLExpression = kiePMMLExpression;
    }

    public static Builder builder(String name,
                                  List<KiePMMLExtension> extensions,
                                  DATA_TYPE dataType,
                                  OP_TYPE opType,
                                  KiePMMLExpression kiePMMLExpression) {
        return new Builder(name, extensions, dataType, opType, kiePMMLExpression);
    }

    public DATA_TYPE getDataType() {
        return dataType;
    }

    public OP_TYPE getOpType() {
        return opType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public KiePMMLExpression getKiePMMLExpression() {
        return kiePMMLExpression;
    }

    public Object evaluate(final ProcessingDTO processingDTO) {
        return commonEvaluate(kiePMMLExpression.evaluate(processingDTO), dataType);
    }

    public static class Builder extends AbstractKiePMMLComponent.Builder<KiePMMLDerivedField> {

        private Builder(String name,
                        List<KiePMMLExtension> extensions,
                        DATA_TYPE dataType,
                        OP_TYPE opType,
                        KiePMMLExpression kiePMMLExpression) {
            super("DerivedField-", () -> new KiePMMLDerivedField(name, extensions, dataType, opType, kiePMMLExpression));
        }

        public Builder withDisplayName(String displayName) {
            if (displayName != null) {
                toBuild.displayName = displayName;
            }
            return this;
        }

    }

}
