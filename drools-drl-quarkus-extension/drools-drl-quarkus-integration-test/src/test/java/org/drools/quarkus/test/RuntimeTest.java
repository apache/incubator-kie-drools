package org.drools.quarkus.test;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.kie.api.definition.KiePackage;
import org.kie.api.runtime.KieRuntimeBuilder;
import org.kie.api.runtime.KieSession;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class RuntimeTest {

    @Inject
    KieRuntimeBuilder runtimeBuilder;

    @Test
    public void testDrlEvaluation() {
        // canDrinkKS is the default session
        testSimpleDrl(runtimeBuilder.newKieSession(), "org.drools.drl");
    }

    @Test
    public void testDTableEvaluation() {
        testSimpleDrl(runtimeBuilder.newKieSession("canDrinkKSDTable"), "org.drools.dtable");
    }

    private void testSimpleDrl(KieSession ksession, String assetPackage) {
        List<String> pkgNames = ksession.getKieBase().getKiePackages().stream().map(KiePackage::getName).collect(Collectors.toList());
        assertEquals(2, pkgNames.size());
        assertTrue(pkgNames.contains("org.drools.quarkus.test"));
        assertTrue(pkgNames.contains(assetPackage));

        Result result = new Result();
        ksession.insert(result);
        ksession.insert(new Person("Mark", 17));
        ksession.fireAllRules();

        assertEquals("Mark can NOT drink", result.toString());
    }
}
