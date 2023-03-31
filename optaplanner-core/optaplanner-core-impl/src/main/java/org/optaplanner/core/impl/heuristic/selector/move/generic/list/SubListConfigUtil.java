package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.optaplanner.core.config.heuristic.selector.list.SubListSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.list.SubListChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.list.SubListSwapMoveSelectorConfig;
import org.slf4j.LoggerFactory;

/**
 * Provides backward-compatible way to handle minimumSubListSize and maximumSubListSize properties used on
 * {@link SubListChangeMoveSelectorConfig} and {@link SubListSwapMoveSelectorConfig}.
 * <p>
 * If the property is used, a warning message is logged. If the corresponding property on a child {@link SubListSelectorConfig}
 * is uninitialized, it will be transferred. Thanks to this, configs that used the properties before they became deprecated
 * will continue working as if they defined the properties at the child selector. The user will be warned about the deprecation.
 * <p>
 * Using both the deprecated and the new property is a mistake and will throw an exception.
 * <p>
 * This class should be removed together with its usages and tests for the property transfer and wrong config detection once
 * the deprecated properties are removed.
 */
final class SubListConfigUtil {

    private SubListConfigUtil() {
    }

    static <Config_> void transferDeprecatedMinimumSubListSize(
            Config_ moveSelectorConfig, Function<Config_, Integer> sourceGetter,
            String subListSelectorRole, SubListSelectorConfig subListSelectorConfig) {
        transferDeprecatedProperty(
                "minimumSubListSize", moveSelectorConfig, sourceGetter,
                subListSelectorRole, subListSelectorConfig,
                SubListSelectorConfig::getMinimumSubListSize,
                SubListSelectorConfig::setMinimumSubListSize);
    }

    static <Config_> void transferDeprecatedMaximumSubListSize(
            Config_ moveSelectorConfig, Function<Config_, Integer> sourceGetter,
            String subListSelectorRole, SubListSelectorConfig subListSelectorConfig) {
        transferDeprecatedProperty(
                "maximumSubListSize",
                moveSelectorConfig, sourceGetter,
                subListSelectorRole, subListSelectorConfig,
                SubListSelectorConfig::getMaximumSubListSize,
                SubListSelectorConfig::setMaximumSubListSize);
    }

    private static <Config_> void transferDeprecatedProperty(
            String propertyName,
            Config_ moveSelectorConfig,
            Function<Config_, Integer> sourceGetter,
            String childConfigName,
            SubListSelectorConfig subListSelectorConfig,
            Function<SubListSelectorConfig, Integer> targetGetter,
            BiConsumer<SubListSelectorConfig, Integer> targetSetter) {
        Integer moveSelectorSubListSize = sourceGetter.apply(moveSelectorConfig);
        if (moveSelectorSubListSize != null) {
            LoggerFactory.getLogger(moveSelectorConfig.getClass()).warn(
                    "{}'s {} property is deprecated. Set {} on the child {}.",
                    moveSelectorConfig.getClass().getSimpleName(), propertyName,
                    propertyName, SubListSelectorConfig.class.getSimpleName());
            Integer subListSize = targetGetter.apply(subListSelectorConfig);
            if (subListSize != null) {
                throw new IllegalArgumentException("The moveSelector (" + moveSelectorConfig
                        + ") and its " + childConfigName + " (" + subListSelectorConfig
                        + ") both set the " + propertyName
                        + ", which is a conflict.\n"
                        + "Use " + SubListSelectorConfig.class.getSimpleName() + "." + propertyName + " only.");
            }
            targetSetter.accept(subListSelectorConfig, moveSelectorSubListSize);
        }
    }
}
