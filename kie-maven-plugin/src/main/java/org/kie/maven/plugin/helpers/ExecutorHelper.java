package org.kie.maven.plugin.helpers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.maven.plugin.logging.Log;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.kie.memorycompiler.JavaConfiguration;

public class ExecutorHelper {

    private ExecutorHelper() {
    }

    public static void setSystemProperties(Map<String, String> properties, Log log) {

        if (properties != null) {
            log.debug("Additional system properties: " + properties);
            for (Map.Entry<String, String> property : properties.entrySet()) {
                if (property.getKey().equals(JavaConfiguration.JAVA_LANG_LEVEL_PROPERTY)) {
                    log.warn("It seems you are setting `" + 
                            JavaConfiguration.JAVA_LANG_LEVEL_PROPERTY + 
                            "` while building a KJAR in a Maven-based build." +
                            " It is recommended to properly set `maven.compiler.release` instead.");
                }
                System.setProperty(property.getKey(), property.getValue());
            }
            log.debug("Configured system properties were successfully set.");
        }
    }

    public static List<String> getFilesByType(InternalKieModule kieModule, String fileType) {
        return kieModule.getFileNames()
                .stream()
                .filter(f -> f.endsWith(fileType))
                .collect(Collectors.toList());
    }
}
