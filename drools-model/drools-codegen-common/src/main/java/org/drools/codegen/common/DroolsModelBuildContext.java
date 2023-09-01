package org.drools.codegen.common;

import java.io.File;
import java.util.Collection;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Predicate;

public interface DroolsModelBuildContext {

    String APPLICATION_PROPERTIES_FILE_NAME = "application.properties";
    String DEFAULT_PACKAGE_NAME = "org.kie.kogito.app";
    String KOGITO_GENERATE_REST = "kogito.generate.rest";
    String KOGITO_GENERATE_DI = "kogito.generate.di";

    Optional<String> getApplicationProperty(String property);

    Collection<String> getApplicationProperties();

    void setApplicationProperty(String key, String value);

    String getPackageName();

    ClassLoader getClassLoader();

    AppPaths getAppPaths();

    String name();

    interface Builder {

        Builder withPackageName(String packageName);

        Builder withApplicationPropertyProvider(DroolsModelApplicationPropertyProvider applicationProperties);

        Builder withApplicationProperties(Properties applicationProperties);

        Builder withApplicationProperties(File... files);

//        Builder withAddonsConfig(AddonsConfig addonsConfig);

        Builder withClassAvailabilityResolver(Predicate<String> classAvailabilityResolver);

        Builder withClassLoader(ClassLoader classLoader);

        Builder withAppPaths(AppPaths appPaths);

//        Builder withGAV(KogitoGAV gav);

        DroolsModelBuildContext build();
    }
}
