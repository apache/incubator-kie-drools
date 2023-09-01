package org.kie.pmml.evaluator.core.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kie.api.pmml.ParameterInfo;

/**
 * Class used to convert data to/from different formats
 */
public class Converter {

    private Converter() {
        // Avoid instantiation
    }

    /**
     * <b>Extract</b> the objects from the <code>ParameterInfo</code> values of the given map.
     *
     * @param parameterMap
     * @return
     */
    public static Map<String, Object> getUnwrappedParametersMap(Map<String, ParameterInfo> parameterMap) {
        Map<String, Object> toReturn = new HashMap<>();
        for (Map.Entry<String, ParameterInfo> entry : parameterMap.entrySet()) {
            toReturn.put(entry.getKey(), entry.getValue().getValue());
        }
        return toReturn;
    }

    /**
     * <b>Extract</b> the objects from the <code>ParameterInfo</code> of the given collection.
     *
     * @param parameterInfos
     * @return
     */
    public static Map<String, Object> getUnwrappedParametersMap(Collection<ParameterInfo> parameterInfos) {
        Map<String, Object> toReturn = new HashMap<>();
        for (ParameterInfo parameterInfo : parameterInfos) {
            toReturn.put(parameterInfo.getName(), parameterInfo.getValue());
        }
        return toReturn;
    }
}
