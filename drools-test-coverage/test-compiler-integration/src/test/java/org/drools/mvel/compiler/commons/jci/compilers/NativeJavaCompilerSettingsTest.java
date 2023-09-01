package org.drools.mvel.compiler.commons.jci.compilers;

import java.util.List;

import org.assertj.core.data.Index;
import org.junit.Test;
import org.kie.memorycompiler.jdknative.NativeJavaCompilerSettings;

import static org.assertj.core.api.Assertions.assertThat;

public class NativeJavaCompilerSettingsTest {

    @Test
    public void defaultSettings() {
        NativeJavaCompilerSettings settings = new NativeJavaCompilerSettings();
        List<String> options = settings.toOptionsList();
        assertThat(options).hasSize(6);
        assertThat(options).contains("-source", "-target", "-encoding");
        // check the order is correct, value of the option needs to be right after the option name
        assertThat(options).contains("1.8", Index.atIndex(options.indexOf("-source") + 1));
        assertThat(options).contains("1.8", Index.atIndex(options.indexOf("-target") + 1));
        assertThat(options).contains("UTF-8", Index.atIndex(options.indexOf("-encoding") + 1));
    }

    @Test
    public void allSettings() {
        NativeJavaCompilerSettings settings = new NativeJavaCompilerSettings();
        settings.setDebug(true);
        settings.setWarnings(true);
        settings.setDeprecations(true);
        settings.setSourceEncoding("My-Custom-Encoding");
        settings.setSourceVersion("1.9");
        settings.setTargetVersion("1.9");
        List<String> options = settings.toOptionsList();

        assertThat(options).hasSize(9);
        assertThat(options).contains("-g");
        assertThat(options).contains("-Xlint:all");
        assertThat(options).contains("-deprecation");
        // check the order is correct, value of the option needs to be right after the option name
        assertThat(options).contains("1.9", Index.atIndex(options.indexOf("-source") + 1));
        assertThat(options).contains("1.9", Index.atIndex(options.indexOf("-target") + 1));
        assertThat(options).contains("My-Custom-Encoding", Index.atIndex(options.indexOf("-encoding") + 1));
    }

}
