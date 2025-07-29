package org.kie.dmn.core.util;

import org.kie.dmn.api.core.DMNVersion;
import org.kie.dmn.core.compiler.DMNTypeRegistry;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;

public class DMNVersionUtil {

    private static final Logger logger = LoggerFactory.getLogger(DMNVersionUtil.class);

    private DMNVersionUtil() {
    }

    /**
     * Dynamically loads and instantiates the latest version of DMNTypeRegistry
     * @return DMNTypeRegistry
     */
    public static DMNTypeRegistry getLatestDMNTypeRegistry() {
        try {
            String dMNTypeRegistryClassName = String.format("org.kie.dmn.core.compiler.DMNTypeRegistryV%s", DMNVersion.getLatest().getDmnVersion());
            logger.warn("DMNTypeRegistry class is {}", dMNTypeRegistryClassName);
            Class<? extends DMNTypeRegistry> registryClass = (Class<?
                    extends DMNTypeRegistry>) Class.forName(dMNTypeRegistryClassName);
            Constructor<? extends DMNTypeRegistry> constructor =
                    registryClass.getConstructor(Map.class);

            return constructor.newInstance(Collections.emptyMap());
        } catch (ClassNotFoundException | IllegalAccessException | ClassCastException | NoSuchMethodException |
                 InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Dynamically loads and instantiates the latest URI_FEEL value reflexively
     * @return string URI_FEEL
     */
    public static String getLatestFeelReflectively() {
        try {
            String kieDMNModelInstrumentedBaseClassName = String.format("org.kie.dmn.model.%s" +
                            ".KieDMNModelInstrumentedBase",
                    DMNVersion.getLatestVersion());
            Class<? extends DMNModelInstrumentedBase> kieDMNModelInstrumentedBaseClass = (Class<?
                    extends DMNModelInstrumentedBase>) Class.forName(kieDMNModelInstrumentedBaseClassName);
            Field uriFeelField = kieDMNModelInstrumentedBaseClass.getField("URI_FEEL");
            return (String) uriFeelField.get(null);
        } catch (NoSuchFieldException e) {
            logger.warn("Failed to retrieve URI_FEEL reflectively");
            return null;
        } catch (ClassNotFoundException | IllegalAccessException | ClassCastException e) {
            throw new RuntimeException(e);
        }
    }
}
