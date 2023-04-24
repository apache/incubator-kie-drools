package org.drools.drlonyaml.todrl;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.assertj.core.api.Assertions;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.DrlParser;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;

public class YAMLtoDRLTest {
    private static final Logger LOG = LoggerFactory.getLogger(YAMLtoDRLTest.class);
    private static final DrlParser drlParser = new DrlParser();
    private static final ObjectMapper mapper;
    static {
        YAMLFactory yamlFactory = YAMLFactory.builder()
                .enable(Feature.MINIMIZE_QUOTES)
                .build();
        mapper = new ObjectMapper(yamlFactory);
    }
    
    private void assertDumpingYAMLtoDRLisValid(String filename) {
        try {
            final String yamlText = Files.readString(Paths.get(YAMLtoDRLTest.class.getResource(filename).toURI()));
            assertThat(yamlText).as("Failed to read test resource")
                .isNotNull();
            
            org.drools.drlonyaml.model.Package readValue = mapper.readValue(yamlText, org.drools.drlonyaml.model.Package.class);
            assertThat(readValue).as("Failed to parse YAML as model")
                .isNotNull();
            
            final String drlText = YAMLtoDrlDumper.dumpDRL(readValue);
            LOG.debug(drlText);
            assertThat(drlText).as("result of DRL dumper shall not be null or empty")
                .isNotNull()
                .isNotEmpty();
            
            PackageDescr parseResult = drlParser.parse(new StringReader(drlText));
            assertThat(parseResult).as("The result of DRL dumper must be syntactically valid DRL")
                .isNotNull();
        } catch (Exception e) {
            Assertions.fail("Failed to generate a valid DRL while processing YAML", e);
        }
    }
    
    @Test
    public void smokeTestFromYAML2() {
        assertDumpingYAMLtoDRLisValid("/smoketests/yaml2.yml");
    }
}
