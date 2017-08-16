package org.drools.pmml.pmml_4_2.model;

import java.io.Serializable;

import org.drools.pmml.pmml_4_2.PMMLDataType;

public abstract class AbstractPMMLData implements PMMLDataType, Serializable {
    private static final long serialVersionUID = 19630331;
    private String modelName;

    protected AbstractPMMLData() {

    }

    protected AbstractPMMLData( String modelName ) {
        this.modelName = modelName;
    }

    public String getModelName() {
        return this.modelName;
    }
}
