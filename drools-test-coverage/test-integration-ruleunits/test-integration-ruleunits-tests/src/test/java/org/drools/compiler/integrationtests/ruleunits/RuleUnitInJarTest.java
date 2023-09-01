package org.drools.compiler.integrationtests.ruleunits;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class RuleUnitInJarTest {

    private static final String TEST_JAR = "test-integration-ruleunits-jar.jar";
    private static final String TEST_UNIT_CLASS = "org.drools.compiler.integrationtests.ruleunits.HelloJarUnit";

    @Test
    public void helloJar() {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try {
            URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{this.getClass().getClassLoader().getResource(TEST_JAR)}, tccl);
            Thread.currentThread().setContextClassLoader(urlClassLoader);
            Class<?> unitClass = urlClassLoader.loadClass(TEST_UNIT_CLASS);
            RuleUnitData unit = (RuleUnitData) unitClass.getDeclaredConstructor().newInstance();
            Method getStringsMethod = unitClass.getMethod("getStrings");
            ((DataStore<String>) getStringsMethod.invoke(unit)).add("Hello Jar");

            try (RuleUnitInstance<RuleUnitData> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit)) {
                assertThat(unitInstance.fire()).isEqualTo(2);
                Method getResultsMethod = unitClass.getMethod("getResults");
                assertThat(((List<String>) getResultsMethod.invoke(unit))).containsExactlyInAnyOrder("it worked!", "it worked in decision table!");
            }
        } catch (NoSuchMethodException | ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
            fail("Fail with reflection", e);
        } finally {
            Thread.currentThread().setContextClassLoader(tccl);
        }
    }
}
