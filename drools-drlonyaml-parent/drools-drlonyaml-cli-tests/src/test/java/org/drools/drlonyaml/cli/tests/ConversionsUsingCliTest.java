package org.drools.drlonyaml.cli.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;

import org.junit.Test;

public class ConversionsUsingCliTest {

    @Test
    public void testDrl2Yaml() throws Exception {
        InputStream generatedResource = this.getClass().getResourceAsStream("/converted.test1.yml");
        assertThat(generatedResource).isNotNull();
        
        String ymlContent = trimUpToThenAndNoWhitespace(generatedResource.readAllBytes());
        String expectedContent = trimUpToThenAndNoWhitespace(this.getClass().getResourceAsStream("/expected/test1.yml").readAllBytes());
        assertThat(ymlContent).as("the converted Yaml matches the expected content)")
            .isEqualTo(expectedContent);
    }
    
    @Test
    public void testYaml2Drl() throws Exception {
        InputStream generatedResource = this.getClass().getResourceAsStream("/converted.test2.drl");
        assertThat(generatedResource).isNotNull();
        
        String ymlContent = trimUpToThenAndNoWhitespace(generatedResource.readAllBytes());
        String expectedContent = trimUpToThenAndNoWhitespace(this.getClass().getResourceAsStream("/expected/test2.drl.txt").readAllBytes());
        assertThat(ymlContent).as("the converted DRL matches the expected content)")
            .isEqualTo(expectedContent);
    }

    /**
     * Due to limitations in the undelying yaml parser and Linux/Win newline (used in "then" RHS part of rule)
     * the test make sure the conversion take place as expected by similarity up to the first available "then".
     */
    private String trimUpToThenAndNoWhitespace(byte[] content) {
        String result = new String(content).strip();
        result = result.substring(0, result.indexOf("then"));
        result = result.replaceAll("\\s+", "");
        return result;
    }
    
    @Test
    public void testBatch2Yaml() {
        assertThat(this.getClass().getResourceAsStream("/batch_drl_files/test1.drl.yml")).isNotNull();
    }
    
    @Test
    public void testBatch2Drl() {
        assertThat(this.getClass().getResourceAsStream("/batch_yml_files/test2.yml.drl")).isNotNull();
    }
}
