package org.kie.dmn.core.compiler;

import org.kie.dmn.core.assembler.DMNAssemblerService;

public class RuntimeTypeCheckOption implements DMNOption {

    private static final long serialVersionUID = 510l;

    public static final String PROPERTY_NAME = DMNAssemblerService.ORG_KIE_DMN_PREFIX + ".runtime.typecheck";

    /**
     * The default value for this option
     */
    public static final boolean DEFAULT_VALUE = false;

    private final boolean runtimeTypeCheck;

    public RuntimeTypeCheckOption(boolean value) {
        this.runtimeTypeCheck = value;
    }

    public RuntimeTypeCheckOption(String value) {
        this.runtimeTypeCheck = value == null ? DEFAULT_VALUE : Boolean.valueOf(value);
    }

    @Override
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public boolean isRuntimeTypeCheck() {
        return runtimeTypeCheck;
    }

}
