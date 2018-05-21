package org.kie.dmn.model.v1_1.extensions;

import org.kie.dmn.model.v1_1.DMNModelInstrumentedBase;

import java.util.ArrayList;
import java.util.List;

public class TestCases extends DMNModelInstrumentedBase {
    private String modelName;
    private List<String> labels;
    private List<TestCase> testCase = new ArrayList<>();

    public List<TestCase> getTestCase() {
        return testCase;
    }

    public void setTestCase(List<TestCase> testCase) {
        this.testCase = testCase;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }
}
