package org.drools.compiler.integrationtests.operators;

import java.util.Collection;
import java.util.HashMap;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.model.functions.NativeImageTestUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.drools.compiler.integrationtests.operators.FromTest.testFromSharingCommon;

@RunWith(Parameterized.class)
public class FromOnlyExecModelTest {

    protected final KieBaseTestConfiguration kieBaseTestConfiguration;

    public FromOnlyExecModelTest(KieBaseTestConfiguration kieBaseTestConfiguration1) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration1;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudOnlyExecModelConfiguration();
    }

    @Test // KOGITO-3771
    public void testFromSharingWithNativeImage() {
        try {
            NativeImageTestUtil.setNativeImage();
            testFromSharingCommon(kieBaseTestConfiguration, new HashMap<>(), 2, 2);
        } finally {
            NativeImageTestUtil.unsetNativeImage();
        }
    }

    // This test that the node sharing isn't working without lambda externalisation
    @Test
    public void testFromSharingWithNativeImageWithoutLambdaExternalisation() {
        try {
            NativeImageTestUtil.setNativeImage();
            HashMap<String, String> properties = new HashMap<>();
            properties.put("drools.externaliseCanonicalModelLambda", Boolean.FALSE.toString());
            testFromSharingCommon(kieBaseTestConfiguration, properties, 3, 1);
        } finally {
            NativeImageTestUtil.unsetNativeImage();
        }
    }
}
