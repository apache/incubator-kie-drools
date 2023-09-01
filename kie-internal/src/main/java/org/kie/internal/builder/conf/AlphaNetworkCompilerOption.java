package org.kie.internal.builder.conf;

import org.kie.api.conf.OptionKey;

/**
 * An Enum for AlphaNetworkCompilerOption option.
 *
 * drools.alphaNetworkCompiler = &lt;disabled|inmemory|load&gt;
 *
 * Disabled: Do not generate compiled alpha network
 * InMemory: Generate compiled alpha network after creation of the kiebase and compile it in-memory
 * Load    : Assume compiled alpha network is already compiled in the kjar, load it from the classpath
 *
 * DEFAULT = disabled
 */
public enum AlphaNetworkCompilerOption implements SingleValueRuleBuilderOption {

    DISABLED("disabled"),
    INMEMORY("inmemory"),
    LOAD("load");

    public static final String PROPERTY_NAME = "drools.alphaNetworkCompiler";

    public static OptionKey<AlphaNetworkCompilerOption> KEY = new OptionKey<>(TYPE, PROPERTY_NAME);

    private String value;

    AlphaNetworkCompilerOption(final String value ) {
        this.value = value;
    }

    public String getMode() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public static AlphaNetworkCompilerOption determineAlphaNetworkCompilerMode(String mode) {
        if ( INMEMORY.getMode().equalsIgnoreCase( mode ) ) {
            return INMEMORY;
        } else if ( DISABLED.getMode().equalsIgnoreCase( mode ) ) {
            return DISABLED;
        } else if ( LOAD.getMode().equalsIgnoreCase(mode ) ) {
            return LOAD;
        }
        throw new IllegalArgumentException( "Illegal enum value '" + mode + "' for AlphaNetworkCompilerOption" );
    }
}
