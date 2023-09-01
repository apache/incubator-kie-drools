package org.kie.dmn.feel.runtime;

import java.util.Collection;

import org.junit.runners.Parameterized;

public class FEELTernaryLogicTest extends BaseFEELTest {

    @Parameterized.Parameters(name = "{3}: {0} ({1}) = {2}")
    public static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                // ternary logic operations as per the spec
                { "true and true", Boolean.TRUE , null},
                { "true and false", Boolean.FALSE , null},
                { "true and null",  null , null},
                { "false and true", Boolean.FALSE , null},
                { "false and false", Boolean.FALSE , null},
                { "false and null", Boolean.FALSE , null},
                { "null and true", null , null},
                { "null and false", Boolean.FALSE , null},
                { "null and null", null , null},
                { "true or true", Boolean.TRUE , null},
                { "true or false", Boolean.TRUE , null},
                { "true or null",  Boolean.TRUE , null},
                { "false or true", Boolean.TRUE , null},
                { "false or false", Boolean.FALSE , null},
                { "false or null", null , null},
                { "null or true", Boolean.TRUE , null},
                { "null or false", null , null},
                { "null or null", null , null},
                // logical operator priority
                { "false and false or true", Boolean.TRUE , null},
                { "false and (false or true)", Boolean.FALSE , null},
                { "true or false and false", Boolean.TRUE , null},
                { "(true or false) and false", Boolean.FALSE , null}
        };
        return addAdditionalParameters(cases, false);
    }
}
