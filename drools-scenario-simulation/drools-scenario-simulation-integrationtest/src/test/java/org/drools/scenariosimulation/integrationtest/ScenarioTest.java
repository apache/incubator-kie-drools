package org.drools.scenariosimulation.integrationtest;

import org.drools.scenariosimulation.backend.runner.ScenarioJunitActivator;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import static org.kie.efesto.common.api.constants.Constants.INDEXFILE_DIRECTORY_PROPERTY;

@org.junit.runner.RunWith(ScenarioJunitActivator.class)
public class ScenarioTest {

    @BeforeClass
    public static void setSystemProperties() {
        System.setProperty(INDEXFILE_DIRECTORY_PROPERTY, "./target/test-classes");
    }

    @AfterClass
    public static void clearSystemProperties() {
        System.clearProperty(INDEXFILE_DIRECTORY_PROPERTY);
    }
}
