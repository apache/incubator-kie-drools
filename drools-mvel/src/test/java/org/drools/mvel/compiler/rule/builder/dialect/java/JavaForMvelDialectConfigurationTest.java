package org.drools.mvel.compiler.rule.builder.dialect.java;

import org.drools.mvel.java.JavaForMvelDialectConfiguration;
import org.junit.Test;

public class JavaForMvelDialectConfigurationTest {

    @Test
    public void checkVersion() {
        new JavaForMvelDialectConfiguration().setJavaLanguageLevel("1.5");
        new JavaForMvelDialectConfiguration().setJavaLanguageLevel("1.6");
        new JavaForMvelDialectConfiguration().setJavaLanguageLevel("1.7");
        new JavaForMvelDialectConfiguration().setJavaLanguageLevel("9");
        new JavaForMvelDialectConfiguration().setJavaLanguageLevel("10");
        new JavaForMvelDialectConfiguration().setJavaLanguageLevel("11");
        new JavaForMvelDialectConfiguration().setJavaLanguageLevel("12");
        new JavaForMvelDialectConfiguration().setJavaLanguageLevel("13");
        new JavaForMvelDialectConfiguration().setJavaLanguageLevel("14");
        new JavaForMvelDialectConfiguration().setJavaLanguageLevel("15");
        new JavaForMvelDialectConfiguration().setJavaLanguageLevel("16");
        new JavaForMvelDialectConfiguration().setJavaLanguageLevel("17");
        new JavaForMvelDialectConfiguration().setJavaLanguageLevel("18");
        new JavaForMvelDialectConfiguration().setJavaLanguageLevel("19");
    }

}