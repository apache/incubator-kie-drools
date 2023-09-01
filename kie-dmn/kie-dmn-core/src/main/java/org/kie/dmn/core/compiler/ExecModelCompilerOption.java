package org.kie.dmn.core.compiler;

import org.kie.dmn.core.assembler.DMNAssemblerService;

public class ExecModelCompilerOption implements DMNOption {

    private static final long serialVersionUID = 510l;

    public static final String PROPERTY_NAME = DMNAssemblerService.ORG_KIE_DMN_PREFIX + ".compiler.execmodel";

    /**
     * The default value for this option
     */
    public static final boolean DEFAULT_VALUE = false;

    private final boolean useExecModelCompiler;

    public ExecModelCompilerOption(boolean value) {
        this.useExecModelCompiler = value;
    }

    public ExecModelCompilerOption(String value) {
        this.useExecModelCompiler = value == null ? DEFAULT_VALUE : Boolean.valueOf(value);
    }

    @Override
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public boolean isUseExecModelCompiler() {
        return useExecModelCompiler;
    }

}
