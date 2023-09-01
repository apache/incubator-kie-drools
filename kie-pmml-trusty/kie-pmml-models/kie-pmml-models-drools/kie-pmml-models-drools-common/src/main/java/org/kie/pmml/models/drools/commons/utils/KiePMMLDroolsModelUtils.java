package org.kie.pmml.models.drools.commons.utils;

import org.kie.pmml.api.enums.DATA_TYPE;

/**
 * Static utility methods for <code>KiePMMLDroolsModel</code>s
 */
public class KiePMMLDroolsModelUtils {

    private KiePMMLDroolsModelUtils() {
        // Avoid instantiation
    }

    /**
     * Return an <code>Object</code> correctly formatted to be put in drl (e.g. if the <b>targetType</b>
     * is <code>DATA_TYPE.STRING</code> returns the <b>quoted</b> rawValue.
     * <p>
     * If <b>rawValue</b> is <code>null</code>, returns <code>null</code>
     * @param rawValue
     * @param targetType
     * @return
     */
    public static Object getCorrectlyFormattedResult(Object rawValue, DATA_TYPE targetType) {
        if (rawValue == null) {
            return null;
        }
        Object toReturn = targetType.getActualValue(rawValue);
        if (DATA_TYPE.STRING.equals(targetType)) {
            toReturn = "\"" + toReturn + "\"";
        }
        return toReturn;
    }
}
