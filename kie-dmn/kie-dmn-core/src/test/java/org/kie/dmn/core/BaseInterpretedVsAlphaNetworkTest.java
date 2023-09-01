package org.kie.dmn.core;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.dmn.core.compiler.AlphaNetworkOption;

@RunWith(Parameterized.class)
public abstract class BaseInterpretedVsAlphaNetworkTest {

    @Parameterized.Parameters(name = "{0}")
    public static Object[] params() {
        return new Object[]{true, false};
    }

    private final boolean alphaNetwork;

    public BaseInterpretedVsAlphaNetworkTest(final boolean useAlphaNetwork) {
        this.alphaNetwork = useAlphaNetwork;
    }

    @Before
    public void before() {
        System.setProperty(AlphaNetworkOption.PROPERTY_NAME, Boolean.toString(alphaNetwork));
    }

    @After
    public void after() {
        System.clearProperty(AlphaNetworkOption.PROPERTY_NAME);
    }
}
