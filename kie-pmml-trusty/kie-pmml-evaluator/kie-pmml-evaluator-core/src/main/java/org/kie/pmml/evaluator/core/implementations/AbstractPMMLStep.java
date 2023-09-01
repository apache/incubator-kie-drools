package org.kie.pmml.evaluator.core.implementations;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.kie.pmml.api.models.PMMLStep;

/**
 * Common abstract implementation of <code>PMMLStep</code>
 */
public class AbstractPMMLStep implements PMMLStep {

    private static final long serialVersionUID = -7633308400272166095L;

    private final Map<String, Object> info = new HashMap<>();

    @Override
    public void addInfo(String infoName, Object infoValue) {
        info.put(infoName, infoValue);
    }

    /**
     * Returns an <b>unmodifiable map</b> of <code>info</code>
     *
     * @return
     */
    @Override
    public Map<String, Object> getInfo() {
        return Collections.unmodifiableMap(info);
    }
}
