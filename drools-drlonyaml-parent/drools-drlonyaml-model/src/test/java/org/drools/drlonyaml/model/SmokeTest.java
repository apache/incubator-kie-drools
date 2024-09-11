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
package org.drools.drlonyaml.model;

import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.DrlParser;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class SmokeTest {
    private static final Logger LOG = LoggerFactory.getLogger(SmokeTest.class);
    private static final DrlParser drlParser = new DrlParser();
    private static final ObjectMapper mapper;

    static {
        YAMLFactory yamlFactory = YAMLFactory.builder().enable(Feature.MINIMIZE_QUOTES).build();
        mapper = new ObjectMapper(yamlFactory);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"/smoketests/smoke1.drl.txt",
            "/smoketests/smoke2.drl.txt",
            "/smoketests/smoke3.drl.txt",
            "/smoketests/smoke4.drl.txt",
            "/smoketests/smoke5.drl.txt",
            "/smoketests/smoke6.drl.txt",
            "/smoketests/smoke7.drl.txt",
            "/smoketests/smoke8.drl.txt",
            "/smoketests/smoke9.drl.txt",
            "/smoketests/smoke10.drl.txt",
            "/smoketests/ruleunit.drl.txt"})
    public void smokeTestFromDrl(String filename ) throws Exception {
        String content = Files.readString(Paths.get(this.getClass().getResource(filename).toURI()));
        assertThat(content).as("Failed to read test resource").isNotNull();
        
        PackageDescr pkgDescr = drlParser.parse(new StringReader(content));
        assertThat(pkgDescr).as("Failed to parse DRL as a PackageDescr").isNotNull();
        
        DrlPackage model = DrlPackage.from(pkgDescr);
        assertThat(model).as("Failed to generate from a PackageDescr a valid model").isNotNull();
        
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, model);
        final String yaml = writer.toString();
        writer.close();            
        LOG.debug("{}", yaml);
        assertThat(yaml).as("resulting YAML shall not be null nor empty").isNotNull().isNotEmpty();
        
        final DrlPackage deserPackage = mapper.readValue(yaml, DrlPackage.class);
        assertThat(deserPackage).usingRecursiveComparison().isEqualTo(model);
    }
    
    
    @Test
    public void smokeTestFromYAML1() throws Exception {
        String content = Files.readString(Paths.get(this.getClass().getResource("/smoketests/yamlfirst_smoke1.yml").toURI()));
        DrlPackage result = mapper.readValue(content, DrlPackage.class);
        LOG.debug("{}", result);
    }
    
    @Disabled("additional RHS types not supported at the moment.")
    @Test
    public void smokeTestFromYAML2() throws Exception {
        String content = Files.readString(Paths.get(this.getClass().getResource("/smoketests/yamlfirst_smoke2.yml").toURI()));
        DrlPackage result = mapper.readValue(content, DrlPackage.class);
        LOG.debug("{}", result);
    }
}
