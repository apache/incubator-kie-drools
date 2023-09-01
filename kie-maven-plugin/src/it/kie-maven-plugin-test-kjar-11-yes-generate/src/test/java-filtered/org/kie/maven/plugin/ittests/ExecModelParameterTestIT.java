package org.kie.maven.plugin.ittests;

import java.net.URL;

import org.assertj.core.api.Assertions;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.modelcompiler.CanonicalKieModule;
import org.junit.Test;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExecModelParameterTestIT {

    private static final String GAV_ARTIFACT_ID = "kie-maven-plugin-test-kjar-11-default";
    private static final String GAV_VERSION = "${org.kie.version}";
    private final static String KBASE_NAME = "SimpleKBase-execmodel";
    private final static String RULE_NAME = "Hello";

    @Test
    public void testWithoutDroolsModelCompilerOnClassPathDoNotRunExecModel() throws Exception {
        KieModule kieModule = fireRule();
        assertThat(kieModule).isNotNull();
        assertTrue(kieModule instanceof CanonicalKieModule);
    }

    private KieModule fireRule() throws Exception {
        final URL targetLocation = ExecModelParameterTestIT.class.getProtectionDomain().getCodeSource().getLocation();
        return ITTestsUtils.fireRule(targetLocation, GAV_ARTIFACT_ID, GAV_VERSION, KBASE_NAME, RULE_NAME);
    }
}