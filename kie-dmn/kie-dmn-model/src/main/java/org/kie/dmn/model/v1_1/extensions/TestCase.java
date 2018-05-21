package org.kie.dmn.model.v1_1.extensions;

import org.kie.dmn.model.v1_1.DMNModelInstrumentedBase;

import java.util.ArrayList;
import java.util.List;

public class TestCase extends DMNModelInstrumentedBase {
    private String description;
    private List<InputNode> inputNode = new ArrayList<>();
    private List<ResultNode> resultNode = new ArrayList<>();
    private String id;
    private String type = "decision";

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<InputNode> getInputNode() {
        return inputNode;
    }

    public void setInputNode(List<InputNode> inputNode) {
        this.inputNode = inputNode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ResultNode> getResultNode() {
        return resultNode;
    }

    public void setResultNode(List<ResultNode> resultNode) {
        this.resultNode = resultNode;
    }
}
