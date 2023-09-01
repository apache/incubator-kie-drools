package org.kie.pmml.api.models;

import java.util.Map;

/**
 * Interface representing a meaningful <b>step</b> of PMML execution.
 * The actual meaning will be implemented on a per-model basis
 */
public interface PMMLStep  {

    void addInfo(String infoName, Object infoValue);

    Map<String, Object> getInfo();

}
