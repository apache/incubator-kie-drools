/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.drlonyaml.todrl;

import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;
import org.assertj.core.api.Assertions;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.DrlParser;
import org.drools.drlonyaml.model.DrlPackage;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

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
            assertThat(yamlText).as("Failed to read test resource").isNotNull();
            
            DrlPackage readValue = mapper.readValue(yamlText, DrlPackage.class);
            assertThat(readValue).as("Failed to parse YAML as model").isNotNull();
            
            final String drlText = YAMLtoDrlDumper.dumpDRL(readValue);
            LOG.debug(drlText);
            assertThat(drlText).as("result of DRL dumper shall not be null or empty").isNotNull().isNotEmpty();
            
            PackageDescr parseResult = drlParser.parse(new StringReader(drlText));
            assertThat(parseResult).as("The result of DRL dumper must be syntactically valid DRL").isNotNull();
        } catch (Exception e) {
            Assertions.fail("Failed to generate a valid DRL while processing YAML", e);
        }
    }
    
    @Test
    public void smokeTestFromYAML2() {
        assertDumpingYAMLtoDRLisValid("/smoketests/yaml2.yml");
    }
    
    @Test
    public void smokeTestFromYAML3() {
        assertDumpingYAMLtoDRLisValid("/smoketests/yaml3.yml");
    }    
    
    @Test
    public void smokeTestFromYAML4() {
        assertDumpingYAMLtoDRLisValid("/smoketests/yaml4.yml");
    }
    
    @Test
    public void smokeTestFromYAML5() {
        assertDumpingYAMLtoDRLisValid("/smoketests/yaml5.yml");
    }
    
    @Test
    public void smokeTestFromYAML6() {
        assertDumpingYAMLtoDRLisValid("/smoketests/yaml6.yml");
    }
    
    @Test
    public void smokeTestFromYAML7() {
        assertDumpingYAMLtoDRLisValid("/smoketests/yaml7.yml");
    }
    
    @Test
    public void smokeTestFromYAML8() {
        assertDumpingYAMLtoDRLisValid("/smoketests/yaml8.yml");
    }
    
    @Test
    public void smokeTestFromYAML9() {
        assertDumpingYAMLtoDRLisValid("/smoketests/yaml9.yml");
    }
    
    @Test
    public void smokeTestFromYAML10() {
        assertDumpingYAMLtoDRLisValid("/smoketests/yaml10.yml");
    }
    
    @Test
    public void smokeTestFromYAML11() {
        assertDumpingYAMLtoDRLisValid("/smoketests/yaml11.yml");
    }

    @Test
    public void smokeTestRuleUnit() {
        assertDumpingYAMLtoDRLisValid("/smoketests/ruleunit.yml");
    }
}
