package org.kie.pmml.models.tree.compiler.utils;

import java.util.UUID;

import static org.kie.pmml.commons.Constants.PACKAGE_CLASS_TEMPLATE;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;

/**
 * Class meant to provide utility methods for <b>KiePMMLTree</b> model
 */
public class KiePMMLTreeModelUtils {

    private KiePMMLTreeModelUtils() {
    }

    public static String createNodeFullClassName(final String packageName) {
        String nodeClassName = createNodeClassName();
        return String.format(PACKAGE_CLASS_TEMPLATE, packageName, nodeClassName);
    }

    public static String createNodeClassName() {
        String rawName = "Node" + UUID.randomUUID();
        return getSanitizedClassName(rawName);
    }
}
