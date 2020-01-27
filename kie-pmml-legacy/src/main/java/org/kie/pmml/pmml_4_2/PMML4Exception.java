package org.kie.pmml.pmml_4_2;


public class PMML4Exception extends Exception {

    private String modelId;

    public PMML4Exception(String modelId, String message) {
        super(message);
        this.modelId = modelId;
    }

    public PMML4Exception(String modelId, String message, Throwable cause) {
        super(message, cause);
        this.modelId = modelId;
    }

    public PMML4Exception(String modelId, Throwable cause) {
        super(cause);
        this.modelId = modelId;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

}
