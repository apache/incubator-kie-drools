package org.kie.pmml.commons.model;

import java.util.Map;

/**
 * Interface used to define if a given <code>KiePMMLModel</code> contains a <b>sources map</b>
 */
public interface HasSourcesMap {

    Map<String, String> getSourcesMap();

    void addSourceMap(String key, String value);

    default boolean isInterpreted() {
        return false;
    }
}
