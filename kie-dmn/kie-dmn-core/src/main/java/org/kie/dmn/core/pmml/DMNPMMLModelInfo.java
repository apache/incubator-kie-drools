package org.kie.dmn.core.pmml;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.kie.dmn.api.core.DMNType;

public class DMNPMMLModelInfo extends PMMLModelInfo {

    private final Map<String, DMNType> inputFields;

    public DMNPMMLModelInfo(String name, Map<String, DMNType> inputFields, Collection<String> outputFields) {
        super(name, inputFields.keySet(), outputFields);
        this.inputFields = Collections.unmodifiableMap(new HashMap<>(inputFields));
    }

    public Map<String, DMNType> getInputFields() {
        return inputFields;
    }

}