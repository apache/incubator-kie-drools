package org.kie.api.conf;

import java.util.Collections;
import java.util.Set;

import org.kie.api.PropertiesConfiguration;

public interface OptionsConfiguration<T extends Option, S extends SingleValueOption, M extends MultiValueOption> extends PropertiesConfiguration {

    void makeImmutable();

    /**
     * Gets an option value
     *
     * @param option the option class for the option being requested
     * @param <C extends T>
     *
     * @return the Option value for the given option. Returns null if option is
     *         not configured.
     */
    <C extends T> void setOption( C option );

    /**
     * Gets an option value
     *
     * @param optionKey SingleValueOption OptionKey to look up the valye for.
     * @param <C extends S> C
     *
     * @return the Option value for the given option. Returns null if option is
     *         not configured.
     */
    <C extends S> C getOption( OptionKey<C> optionKey );

    /**
     * Gets an option value for the given option + key. This method should
     * be used for multi-value options, like accumulate functions configuration
     * where one option has multiple values, distinguished by a sub-key.
     *
     * @param optionKey the option class for the option being requested
     * @param subKey the key for the option being requested
     * @param <C extends M> C
     *
     * @return the Option value for the given option + key. Returns null if option is
     *         not configured.
     */
    default <C extends M> C getOption( OptionKey<C> optionKey, String subKey ) {
        return null; // not all configurations have multi options,
    }

    /**
     * Deprecated, KEY now exists top level, and its preferred to be explicit that this is a sub key
     * @param optionKey
     * @return
     * @param <C>
     */
    @Deprecated
    default <C extends M> Set<String> getOptionKeys(OptionKey<C> optionKey) {
        return getOptionSubKeys(optionKey);
    }

    /**
     * Retrieves the set of all sub keys for a MultiValueOption.
     *
     * @param optionKey The OptionKey for the MultiValueOption
     * @return a Set of Strings
     */
    default <C extends M> Set<String> getOptionSubKeys(OptionKey<C> optionKey) {
        return Collections.emptySet();
    }

    ClassLoader getClassLoader();

    <X extends OptionsConfiguration<T, S, M>> X as(ConfigurationKey<X> configuration);

}
