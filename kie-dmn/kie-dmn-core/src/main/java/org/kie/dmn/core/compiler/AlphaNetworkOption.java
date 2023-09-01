package org.kie.dmn.core.compiler;

import org.kie.dmn.core.assembler.DMNAssemblerService;

public class AlphaNetworkOption implements DMNOption {

    private static final long serialVersionUID = 510l;

    public static final String PROPERTY_NAME = DMNAssemblerService.ORG_KIE_DMN_PREFIX + ".compiler.alphanetwork";

    /**
     * The default value for this option
     */
    public static final boolean DEFAULT_VALUE = false;

    private final boolean useAlphaNetwork;

    public AlphaNetworkOption(boolean value) {
        this.useAlphaNetwork = value;
    }

    public AlphaNetworkOption(String value) {
        this.useAlphaNetwork = value == null ? DEFAULT_VALUE : Boolean.valueOf(value);
    }

    @Override
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public boolean isUseAlphaNetwork() {
        return useAlphaNetwork;
    }

}
