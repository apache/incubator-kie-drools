package org.drools.compiler.rule.builder.dialect.java;

import org.junit.Test;

public class JavaDialectConfigurationTest {

    @Test
    public void checkVersion() {
        new JavaDialectConfiguration().setJavaLanguageLevel("1.5");
        new JavaDialectConfiguration().setJavaLanguageLevel("1.6");
        new JavaDialectConfiguration().setJavaLanguageLevel("1.7");
        new JavaDialectConfiguration().setJavaLanguageLevel("9");
        new JavaDialectConfiguration().setJavaLanguageLevel("10");
        new JavaDialectConfiguration().setJavaLanguageLevel("11");
        new JavaDialectConfiguration().setJavaLanguageLevel("12");
    }

    @Test(expected = RuntimeException.class)
    public void java13NotSupported() {
        new JavaDialectConfiguration().setJavaLanguageLevel("13");
    }

}