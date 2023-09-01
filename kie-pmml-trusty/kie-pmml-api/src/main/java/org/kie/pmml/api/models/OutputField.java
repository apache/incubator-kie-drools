package org.kie.pmml.api.models;

import java.io.Serializable;
import java.util.List;

import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.api.enums.RESULT_FEATURE;

/**
 * User-friendly representation of an <b>OutputField</b>
 */
public class OutputField implements Serializable {

    private static final long serialVersionUID = 9103824387333264568L;
    private final String name;
    private final OP_TYPE opType;
    private final DATA_TYPE dataType;
    private final String targetField;
    private final RESULT_FEATURE resultFeature;
    private final List<String> allowedValues;

    public OutputField(final String name,
                       final OP_TYPE opType,
                       final DATA_TYPE dataType,
                       final String targetField,
                       final RESULT_FEATURE resultFeature,
                       final List<String> allowedValues) {
        this.name = name;
        this.opType = opType;
        this.dataType = dataType;
        this.targetField = targetField;
        this.resultFeature = resultFeature;
        this.allowedValues = allowedValues;
    }

    public String getName() {
        return name;
    }

    public OP_TYPE getOpType() {
        return opType;
    }

    public DATA_TYPE getDataType() {
        return dataType;
    }

    public String getTargetField() {
        return targetField;
    }

    public RESULT_FEATURE getResultFeature() {
        return resultFeature;
    }

    public List<String> getAllowedValues() {
        return allowedValues;
    }
}
