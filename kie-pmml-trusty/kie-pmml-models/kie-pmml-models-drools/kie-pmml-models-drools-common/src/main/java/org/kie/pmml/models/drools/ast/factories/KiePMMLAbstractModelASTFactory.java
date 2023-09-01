package org.kie.pmml.models.drools.ast.factories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class to be extended to generate a <code>KiePMMLDroolsAST</code> out of a <code>DataDictionary</code> and a <b>model</b>
 */
public abstract class KiePMMLAbstractModelASTFactory {

    public static final String SURROGATE_RULENAME_PATTERN = "%s_surrogate_%s";
    public static final String SURROGATE_GROUP_PATTERN = "%s_surrogate";
    public static final String STATUS_NULL = "status == null";
    public static final String STATUS_PATTERN = "status == \"%s\"";
    public static final String PATH_PATTERN = "%s_%s";
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLAbstractModelASTFactory.class.getName());

    protected KiePMMLAbstractModelASTFactory() {
        // Avoid instantiation
    }
}
