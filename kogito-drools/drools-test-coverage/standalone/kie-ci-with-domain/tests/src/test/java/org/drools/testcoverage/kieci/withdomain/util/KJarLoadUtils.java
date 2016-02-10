package org.drools.testcoverage.kieci.withdomain.util;

import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;

import java.util.Properties;

/**
 * Common utilities for loading KJar used in tests.
 */
public class KJarLoadUtils {

    private static final KieServices KS = KieServices.Factory.get();

    private KJarLoadUtils() {
    }

    /**
     * Returns ReleaseId (GAV) of the kjar specified in given resource as properties 'groupId', 'artifactId', and 'version'.
     * Some GAV components (typically 'version') are filtered by Maven, so it is useful to store them in resource files.
     *
     * @param resourceName the name of the resource containing GAV as a Java properties file
     * @param loaderClass Class whose ClassLoader will be used for loading the properties file
     * @return ReleaseId representing GAV of the KJAR
     */
    public static ReleaseId loadKJarGAV(final String resourceName, final Class loaderClass) {
        final Properties props = new Properties();
        try {
            props.load(loaderClass.getResourceAsStream(resourceName));
        } catch (Exception e) {
            throw new RuntimeException("Unable to load test kjar GAV from props file.", e);
        }

        return KS.newReleaseId(props.getProperty("groupId"), props.getProperty("artifactId"), props.getProperty("version"));
    }

}
