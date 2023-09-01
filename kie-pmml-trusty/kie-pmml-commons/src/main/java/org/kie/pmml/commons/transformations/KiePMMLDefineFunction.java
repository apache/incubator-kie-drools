package org.kie.pmml.commons.transformations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.ProcessingDTO;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;
import org.kie.pmml.commons.model.expressions.KiePMMLExpression;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;

import static org.kie.pmml.commons.utils.KiePMMLModelUtils.commonEvaluate;

/**
 * @see <a href=http://dmg.org/pmml/v4-4-1/Functions.html#xsdElement_DefineFunction>DefineFunction</a>
 */
public class KiePMMLDefineFunction extends AbstractKiePMMLComponent implements Serializable {

    private static final long serialVersionUID = 9187889880911645935L;
    private final OP_TYPE opType;
    private final List<KiePMMLParameterField> parameterFields;
    private final KiePMMLExpression kiePMMLExpression;
    private DATA_TYPE dataType;

    public KiePMMLDefineFunction(String name,
                                 List<KiePMMLExtension> extensions,
                                 DATA_TYPE dataType,
                                 OP_TYPE opType,
                                 List<KiePMMLParameterField> parameterFields,
                                 KiePMMLExpression kiePMMLExpression) {
        super(name, extensions);
        this.dataType = dataType;
        this.opType = opType;
        this.parameterFields = parameterFields;
        this.kiePMMLExpression = kiePMMLExpression;
    }

    public OP_TYPE getOpType() {
        return opType;
    }

    public List<KiePMMLParameterField> getParameterFields() {
        return Collections.unmodifiableList(parameterFields);
    }

    public DATA_TYPE getDataType() {
        return dataType;
    }

    public KiePMMLExpression getKiePMMLExpression() {
        return kiePMMLExpression;
    }

    public Object evaluate(final ProcessingDTO processingDTO,
                           final List<Object> paramValues) {
        final List<KiePMMLNameValue> kiePMMLNameValues = new ArrayList<>();
        if (parameterFields != null) {
            if (paramValues == null || paramValues.size() < parameterFields.size()) {
                throw new IllegalArgumentException("Expected at least " + parameterFields.size() + " arguments for " + name + " DefineFunction");
            }
            for (int i = 0; i < parameterFields.size(); i++) {
                kiePMMLNameValues.add(new KiePMMLNameValue(parameterFields.get(i).getName(), paramValues.get(i)));
            }
        }
        for (KiePMMLNameValue kiePMMLNameValue : kiePMMLNameValues) {
            processingDTO.addKiePMMLNameValue(kiePMMLNameValue);
        }
        return commonEvaluate(kiePMMLExpression.evaluate(processingDTO), dataType);
    }
}
