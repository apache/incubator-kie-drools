package org.drools.mvel.compiler.kie.builder.impl;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import org.drools.compiler.kie.builder.impl.ClasspathKieProject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class ClasspathKieProjectTransformUrlToFileSystemPathTest {

    @Parameterized.Parameters(name = "URL={0}, expectedPath={1}")
    public static Collection<Object[]> data() throws Exception {
        return Arrays.asList(new Object[][] {
                { new URL("file:/some-path-to-the-module/target/test-classes"), "/some-path-to-the-module/target/test-classes" },
                { new URL("file:/some-path-to-the-module/target/test-classes/META-INF/kmodule.xml"), "/some-path-to-the-module/target/test-classes" },
                { new URL("jar:file:/C:/proj/parser/jar/parser.jar!/test.xml"), "/C:/proj/parser/jar/parser.jar" }
        });
    }
    @Parameterized.Parameter(0)
    public URL url;

    @Parameterized.Parameter(1)
    public String expectedPath;


    @Test
    public void testTransformUrl() {
        String actualPath = ClasspathKieProject.fixURLFromKProjectPath(url);
        assertThat(actualPath).isEqualTo(expectedPath);
    }
}
