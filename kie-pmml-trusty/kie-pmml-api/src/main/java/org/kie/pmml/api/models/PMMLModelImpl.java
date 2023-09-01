package org.kie.pmml.api.models;

import java.util.List;

/**
 * User-friendly representation of a <b>PMML</b> model
 */
public class PMMLModelImpl implements PMMLModel {

    private final String fileName;
    private final String name;
    private final List<MiningField> miningFields;
    private final List<OutputField> outputFields;

    public PMMLModelImpl(String fileName, String name, List<MiningField> miningFields, List<OutputField> outputFields) {
        this.fileName = fileName;
        this.name = name;
        this.miningFields = miningFields;
        this.outputFields = outputFields;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<MiningField> getMiningFields() {
        return miningFields;
    }

    @Override
    public List<OutputField> getOutputFields() {
        return outputFields;
    }
}
