package org.drools.drlonyaml.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.assertj.core.api.Assertions;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.DrlParser;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;

public class SmokeTest {
    private static final Logger LOG = LoggerFactory.getLogger(SmokeTest.class);
    private static final DrlParser drlParser = new DrlParser();
    private static final ObjectMapper mapper;
    static {
        YAMLFactory yamlFactory = YAMLFactory.builder()
                .enable(Feature.MINIMIZE_QUOTES)
                .build();
        mapper = new ObjectMapper(yamlFactory);
    }

    private void assertDrlToYamlAndBack(String filename) {
        try {
            String content = Files.readString(Paths.get(this.getClass().getResource(filename).toURI()));
            PackageDescr pkgDescr = drlParser.parse(new StringReader(content));
            Package model = Package.from(pkgDescr);
            StringWriter writer = new StringWriter();
            mapper.writeValue(writer, model);
            final String yaml = writer.toString();
            writer.close();            
            LOG.debug("{}", yaml);
            final Package deserPackage = mapper.readValue(yaml, Package.class);
            assertThat(deserPackage).usingRecursiveComparison()
                .isEqualTo(model);
        } catch (Exception e) {
            Assertions.fail("Failed to roundtrip from DRL to YAML and back to YAML", e);
        }
    }
    
    @Test
    public void smokeTestFromDRL1() {
        String filename = "/smoketests/smoke1.drl.txt";
        assertDrlToYamlAndBack(filename);
    }

    @Test
    public void smokeTestFromDRL2() {
        String filename = "/smoketests/smoke2.drl.txt";
        assertDrlToYamlAndBack(filename);
    }
    
    @Test
    public void smokeTestFromDRL3() {
        String filename = "/smoketests/smoke3.drl.txt";
        assertDrlToYamlAndBack(filename);
    }
    
    @Test
    public void smokeTestFromDRL4() {
        String filename = "/smoketests/smoke4.drl.txt";
        assertDrlToYamlAndBack(filename);
    }
    
    @Test
    public void smokeTestFromDRL5() {
        String filename = "/smoketests/smoke5.drl.txt";
        assertDrlToYamlAndBack(filename);
    }
    
    @Test
    public void smokeTestFromDRL6() {
        String filename = "/smoketests/smoke6.drl.txt";
        assertDrlToYamlAndBack(filename);
    }
    
    @Test
    public void smokeTestFromDRL7() {
        String filename = "/smoketests/smoke7.drl.txt";
        assertDrlToYamlAndBack(filename);
    }
    
    @Test
    public void smokeTestFromDRL8() {
        String filename = "/smoketests/smoke8.drl.txt";
        assertDrlToYamlAndBack(filename);
    }
    
    @Test
    public void smokeTestFromDRL9() {
        String filename = "/smoketests/smoke9.drl.txt";
        assertDrlToYamlAndBack(filename);
    }
    
    @Test
    public void smokeTestFromDRL10() {
        String filename = "/smoketests/smoke10.drl.txt";
        assertDrlToYamlAndBack(filename);
    }
    
    @Test
    public void smokeTestFromYAML1() throws Exception {
        String content = Files.readString(Paths.get(this.getClass().getResource("/smoketests/yamlfirst_smoke1.yml").toURI()));
        Package result = mapper.readValue(content, Package.class);
        LOG.debug("{}", result);
    }
    
    @Ignore("additional RHS types not supported.")
    @Test
    public void smokeTestFromYAML2() throws Exception {
        String content = Files.readString(Paths.get(this.getClass().getResource("/smoketests/yamlfirst_smoke2.yml").toURI()));
        Package result = mapper.readValue(content, Package.class);
        LOG.debug("{}", result);
    }
}
