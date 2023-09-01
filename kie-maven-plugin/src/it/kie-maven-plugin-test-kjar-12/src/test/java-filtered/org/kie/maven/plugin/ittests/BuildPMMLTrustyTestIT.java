package org.kie.maven.plugin.ittests;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class BuildPMMLTrustyTestIT {

    private static final String GAV_ARTIFACT_ID = "kie-maven-plugin-test-kjar-12";
    private static final String GAV_VERSION = "${org.kie.version}";

    private static final String PMML_FILE_NAME = "logisticregressionirisdata/logisticRegressionIrisData.pmml";

    private static final String INDEX_FILE_NAME = "IndexFile.pmml_json";
    private static final List<String> EXAMPLE_PMML_CLASSES = Arrays.asList("compoundnestedpredicatescorecard/CompoundNestedPredicateScorecardFactory.class");

    @Test
    public void testContentKjarWithPMML() throws Exception {
        final URL targetLocation = BuildPMMLTrustyTestIT.class.getProtectionDomain().getCodeSource().getLocation();
        final File kjarFile = ITTestsUtils.getKjarFile(targetLocation, GAV_ARTIFACT_ID, GAV_VERSION);

        final JarFile jarFile = new JarFile(kjarFile);
        final Set<String> jarContent = new HashSet<>();
        final Enumeration<JarEntry> kjarEntries = jarFile.entries();
        while (kjarEntries.hasMoreElements()) {
            final String entryName = kjarEntries.nextElement().getName();
            jarContent.add(entryName);
        }

        Assertions.assertThat(jarContent).isNotEmpty();
        Assertions.assertThat(jarContent).contains(PMML_FILE_NAME);
        Assertions.assertThat(jarContent).contains(INDEX_FILE_NAME);
        EXAMPLE_PMML_CLASSES.forEach(examplePMMLClass ->  Assertions.assertThat(jarContent).contains(examplePMMLClass));
    }
}