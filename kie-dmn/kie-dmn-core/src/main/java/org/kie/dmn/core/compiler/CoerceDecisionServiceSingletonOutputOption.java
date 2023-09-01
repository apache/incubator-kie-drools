package org.kie.dmn.core.compiler;

import org.kie.dmn.core.assembler.DMNAssemblerService;

/**
 * coerce singleton output decision service as a value instead of a context of single entry.
 */
public class CoerceDecisionServiceSingletonOutputOption implements DMNOption {

    private static final long serialVersionUID = 510l;

    public static final String PROPERTY_NAME = DMNAssemblerService.ORG_KIE_DMN_PREFIX + ".decisionservice.coercesingleton";

    /**
     * The default value for this option
     */
    public static final boolean DEFAULT_VALUE = true;

    private final boolean coerceSingleton;

    public CoerceDecisionServiceSingletonOutputOption(boolean value) {
        this.coerceSingleton = value;
    }

    public CoerceDecisionServiceSingletonOutputOption(String value) {
        this.coerceSingleton = value == null ? DEFAULT_VALUE : Boolean.valueOf(value);
    }

    @Override
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public boolean isCoerceSingleton() {
        return coerceSingleton;
    }
}
